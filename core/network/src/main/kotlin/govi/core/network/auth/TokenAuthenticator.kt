package govi.core.network.auth

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Thread-safe OkHttp [Authenticator] that handles HTTP 401 Unauthorized challenges.
 *
 * Implements token refresh with synchronization to prevent thundering-herd/stampede
 * refresh requests when multiple concurrent network calls encounter 401s simultaneously.
 */
class TokenAuthenticator(
    private val authTokenProvider: AuthTokenProvider,
) : Authenticator {
    private val refreshMutex = Mutex()

    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        // Prevent infinite loops if authentication repeatedly fails
        if (responseCount(response) >= MAX_RETRY_ATTEMPTS) {
            runBlocking { authTokenProvider.onAuthFailed() }
            return null
        }

        val failedAuthorizationHeader = response.request.header(AuthInterceptor.HEADER_AUTHORIZATION)

        return runBlocking {
            refreshMutex.withLock {
                val currentToken = authTokenProvider.getAccessToken()
                val currentBearer =
                    if (!currentToken.isNullOrBlank()) {
                        "${AuthInterceptor.BEARER_PREFIX} $currentToken"
                    } else {
                        null
                    }

                // If another concurrent thread has already refreshed the token,
                // retry immediately with the freshly acquired token
                if (currentBearer != null && currentBearer != failedAuthorizationHeader) {
                    return@withLock response.request
                        .newBuilder()
                        .header(AuthInterceptor.HEADER_AUTHORIZATION, currentBearer)
                        .build()
                }

                // Otherwise, perform the token refresh
                val refreshResult = authTokenProvider.refreshToken()
                refreshResult.fold(
                    onSuccess = { newToken ->
                        response.request
                            .newBuilder()
                            .header(
                                AuthInterceptor.HEADER_AUTHORIZATION,
                                "${AuthInterceptor.BEARER_PREFIX} $newToken",
                            ).build()
                    },
                    onFailure = {
                        authTokenProvider.onAuthFailed()
                        null
                    },
                )
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3
    }
}
