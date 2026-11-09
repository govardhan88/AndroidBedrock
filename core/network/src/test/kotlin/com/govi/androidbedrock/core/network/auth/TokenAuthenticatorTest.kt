package com.govi.androidbedrock.core.network.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class TokenAuthenticatorTest {
    @Test
    fun `successful refresh updates authorization header and returns retried request`() {
        val fakeProvider =
            FakeAuthTokenProvider(
                initialToken = "expired_token",
                refreshedToken = "fresh_token_123",
            )
        val authenticator = TokenAuthenticator(fakeProvider)

        val request =
            Request
                .Builder()
                .url("https://api.example.com/users")
                .header(AuthInterceptor.HEADER_AUTHORIZATION, "Bearer expired_token")
                .build()

        val response =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Unauthorized")
                .build()

        val retryRequest = authenticator.authenticate(null, response)

        assertNotNull(retryRequest)
        assertEquals("Bearer fresh_token_123", retryRequest?.header(AuthInterceptor.HEADER_AUTHORIZATION))
        assertEquals(1, fakeProvider.refreshCallCount.get())
        assertEquals(0, fakeProvider.authFailedCallCount.get())
    }

    @Test
    fun `concurrent 401s only trigger a single token refresh call`() =
        runBlocking {
            val fakeProvider =
                FakeAuthTokenProvider(
                    initialToken = "expired_token",
                    refreshedToken = "fresh_token_concurrency",
                    refreshDelayMs = 50L,
                )
            val authenticator = TokenAuthenticator(fakeProvider)

            // Launch 8 concurrent requests encountering 401 with the same expired token
            val deferredList =
                (1..8).map {
                    async(Dispatchers.IO) {
                        val request =
                            Request
                                .Builder()
                                .url("https://api.example.com/data")
                                .header(AuthInterceptor.HEADER_AUTHORIZATION, "Bearer expired_token")
                                .build()

                        val response =
                            Response
                                .Builder()
                                .request(request)
                                .protocol(Protocol.HTTP_1_1)
                                .code(401)
                                .message("Unauthorized")
                                .build()

                        authenticator.authenticate(null, response)
                    }
                }

            val results = deferredList.awaitAll()

            // Exactly ONE refresh API call was executed
            assertEquals(1, fakeProvider.refreshCallCount.get())

            // All 8 requests successfully got the new token
            results.forEach { retryRequest ->
                assertNotNull(retryRequest)
                assertEquals(
                    "Bearer fresh_token_concurrency",
                    retryRequest?.header(AuthInterceptor.HEADER_AUTHORIZATION),
                )
            }
        }

    @Test
    fun `failed refresh returns null and invokes onAuthFailed`() {
        val fakeProvider =
            FakeAuthTokenProvider(
                initialToken = "expired_token",
                shouldRefreshSucceed = false,
            )
        val authenticator = TokenAuthenticator(fakeProvider)

        val request =
            Request
                .Builder()
                .url("https://api.example.com/account")
                .header(AuthInterceptor.HEADER_AUTHORIZATION, "Bearer expired_token")
                .build()

        val response =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Unauthorized")
                .build()

        val retryRequest = authenticator.authenticate(null, response)

        assertNull(retryRequest)
        assertEquals(1, fakeProvider.refreshCallCount.get())
        assertEquals(1, fakeProvider.authFailedCallCount.get())
    }

    @Test
    fun `max retry limit reached stops infinite loop and calls onAuthFailed`() {
        val fakeProvider =
            FakeAuthTokenProvider(
                initialToken = "token",
                refreshedToken = "new_token",
            )
        val authenticator = TokenAuthenticator(fakeProvider)

        val request =
            Request
                .Builder()
                .url("https://api.example.com/loop")
                .header(AuthInterceptor.HEADER_AUTHORIZATION, "Bearer token")
                .build()

        val response1 =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Unauthorized")
                .build()

        val response2 =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Unauthorized")
                .priorResponse(response1)
                .build()

        val response3 =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Unauthorized")
                .priorResponse(response2)
                .build()

        val retryRequest = authenticator.authenticate(null, response3)

        assertNull(retryRequest)
        assertEquals(0, fakeProvider.refreshCallCount.get())
        assertEquals(1, fakeProvider.authFailedCallCount.get())
    }

    private class FakeAuthTokenProvider(
        private var initialToken: String? = null,
        private val refreshedToken: String = "refreshed_token",
        private val shouldRefreshSucceed: Boolean = true,
        private val refreshDelayMs: Long = 0L,
    ) : AuthTokenProvider {
        val refreshCallCount = AtomicInteger(0)
        val authFailedCallCount = AtomicInteger(0)

        override suspend fun getAccessToken(): String? = initialToken

        override suspend fun refreshToken(): Result<String> {
            refreshCallCount.incrementAndGet()
            if (refreshDelayMs > 0) delay(refreshDelayMs)
            return if (shouldRefreshSucceed) {
                initialToken = refreshedToken
                Result.success(refreshedToken)
            } else {
                Result.failure(Exception("Refresh token expired"))
            }
        }

        override suspend fun onAuthFailed() {
            authFailedCallCount.incrementAndGet()
        }
    }
}
