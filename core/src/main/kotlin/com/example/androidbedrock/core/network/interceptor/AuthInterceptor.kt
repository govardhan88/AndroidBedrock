package com.example.androidbedrock.core.network.interceptor

import com.example.androidbedrock.core.network.AuthTokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * OkHttp interceptor for adding authentication headers to requests
 */
class AuthInterceptor @Inject constructor(
    private val authTokenManager: AuthTokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth header for certain endpoints if needed
        if (originalRequest.header("X-Skip-Auth") != null) {
            return chain.proceed(originalRequest.newBuilder().removeHeader("X-Skip-Auth").build())
        }

        val token = authTokenManager.getAccessToken()
        if (token != null) {
            val authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(authenticatedRequest)
        }

        return chain.proceed(originalRequest)
    }
}
