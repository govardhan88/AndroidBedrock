package govi.core.network.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor that attaches the Bearer authentication token to outgoing HTTP requests.
 *
 * To bypass authentication for public endpoints (such as login or registration),
 * add `@Headers("${AuthInterceptor.HEADER_NO_AUTH}: true")` to the Retrofit service method.
 */
class AuthInterceptor(
    private val authTokenProvider: AuthTokenProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Check if the request explicitly opts out of authentication
        if (originalRequest.header(HEADER_NO_AUTH) != null) {
            val strippedRequest =
                originalRequest
                    .newBuilder()
                    .removeHeader(HEADER_NO_AUTH)
                    .build()
            return chain.proceed(strippedRequest)
        }

        val token = runBlocking { authTokenProvider.getAccessToken() }
        val authenticatedRequest =
            if (!token.isNullOrBlank()) {
                originalRequest
                    .newBuilder()
                    .header(HEADER_AUTHORIZATION, "$BEARER_PREFIX $token")
                    .build()
            } else {
                originalRequest
            }

        return chain.proceed(authenticatedRequest)
    }

    companion object {
        const val HEADER_NO_AUTH = "No-Authentication"
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer"
    }
}
