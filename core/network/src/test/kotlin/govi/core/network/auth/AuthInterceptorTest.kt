package govi.core.network.auth

import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.concurrent.TimeUnit

class AuthInterceptorTest {
    @Test
    fun `attaches bearer token when valid token exists`() {
        val fakeProvider =
            object : AuthTokenProvider {
                override suspend fun getAccessToken(): String = "secret_access_token_123"

                override suspend fun refreshToken(): Result<String> = Result.success("secret_access_token_123")

                override suspend fun onAuthFailed() {}
            }
        val interceptor = AuthInterceptor(fakeProvider)

        val request =
            Request
                .Builder()
                .url("https://api.example.com/protected")
                .build()

        val chain = FakeChain(request)
        interceptor.intercept(chain)

        val executedRequest = chain.executedRequest
        assertEquals("Bearer secret_access_token_123", executedRequest?.header(AuthInterceptor.HEADER_AUTHORIZATION))
    }

    @Test
    fun `does not attach header when token is null or blank`() {
        val fakeProvider =
            object : AuthTokenProvider {
                override suspend fun getAccessToken(): String? = null

                override suspend fun refreshToken(): Result<String> = Result.failure(Exception())

                override suspend fun onAuthFailed() {}
            }
        val interceptor = AuthInterceptor(fakeProvider)

        val request =
            Request
                .Builder()
                .url("https://api.example.com/endpoint")
                .build()

        val chain = FakeChain(request)
        interceptor.intercept(chain)

        val executedRequest = chain.executedRequest
        assertNull(executedRequest?.header(AuthInterceptor.HEADER_AUTHORIZATION))
    }

    @Test
    fun `bypasses authentication and removes No-Authentication header`() {
        val fakeProvider =
            object : AuthTokenProvider {
                override suspend fun getAccessToken(): String = "should_not_be_attached"

                override suspend fun refreshToken(): Result<String> = Result.success("should_not_be_attached")

                override suspend fun onAuthFailed() {}
            }
        val interceptor = AuthInterceptor(fakeProvider)

        val request =
            Request
                .Builder()
                .url("https://api.example.com/public-login")
                .header(AuthInterceptor.HEADER_NO_AUTH, "true")
                .build()

        val chain = FakeChain(request)
        interceptor.intercept(chain)

        val executedRequest = chain.executedRequest
        assertNull(executedRequest?.header(AuthInterceptor.HEADER_AUTHORIZATION))
        assertNull(executedRequest?.header(AuthInterceptor.HEADER_NO_AUTH))
    }

    private class FakeChain(
        private val originalRequest: Request,
    ) : Interceptor.Chain {
        var executedRequest: Request? = null

        override fun request(): Request = originalRequest

        override fun proceed(request: Request): Response {
            executedRequest = request
            return Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()
        }

        override fun connection(): Connection? = null

        override fun call(): Call = throw UnsupportedOperationException()

        override fun connectTimeoutMillis(): Int = 0

        override fun withConnectTimeout(
            timeout: Int,
            unit: TimeUnit,
        ): Interceptor.Chain = this

        override fun readTimeoutMillis(): Int = 0

        override fun withReadTimeout(
            timeout: Int,
            unit: TimeUnit,
        ): Interceptor.Chain = this

        override fun writeTimeoutMillis(): Int = 0

        override fun withWriteTimeout(
            timeout: Int,
            unit: TimeUnit,
        ): Interceptor.Chain = this
    }
}
