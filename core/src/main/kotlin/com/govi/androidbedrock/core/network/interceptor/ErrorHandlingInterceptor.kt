package com.govi.androidbedrock.core.network.interceptor

import com.govi.androidbedrock.core.network.ApiException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * OkHttp interceptor for centralized error handling
 */
class ErrorHandlingInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val response = chain.proceed(chain.request())
            
            if (!response.isSuccessful) {
                val errorBody = response.body?.string() ?: ""
                throw ApiException.HttpException(
                    code = response.code,
                    message = response.message,
                    body = errorBody
                )
            }
            response
        } catch (e: IOException) {
            throw ApiException.NetworkException(
                message = e.message ?: "Network error occurred",
                cause = e
            )
        }
    }
}
