package com.govi.androidbedrock.core.auth

import com.govi.androidbedrock.core.utils.NetworkConstants
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
        if (originalRequest.header(NetworkConstants.HEADER_SKIP_AUTH) != null) {
            return chain.proceed(originalRequest.newBuilder().removeHeader(NetworkConstants.HEADER_SKIP_AUTH).build())
        }

        val token = authTokenManager.getAccessToken()
        if (token != null) {
            val authenticatedRequest = originalRequest.newBuilder()
                .header(NetworkConstants.HEADER_AUTHORIZATION, "${NetworkConstants.HEADER_BEARER} $token")
                .build()
            return chain.proceed(authenticatedRequest)
        }

        return chain.proceed(originalRequest)
    }
}
