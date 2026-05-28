package com.govi.androidbedrock.core.network.interceptor

import com.govi.androidbedrock.core.network.BaseUrlProvider
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor that dynamically changes the base URL of a request.
 * Useful for multi-region support or environment switching at runtime.
 */
@Singleton
class DynamicBaseUrlInterceptor @Inject constructor(
    private val baseUrlProvider: BaseUrlProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        
        // Check if dynamic URL is enabled for this request (optional)
        // Or just always apply the current base URL from provider
        val newBaseUrl = baseUrlProvider.getBaseUrl().toHttpUrlOrNull()
        
        if (newBaseUrl != null) {
            val newUrl = request.url.newBuilder()
                .scheme(newBaseUrl.scheme)
                .host(newBaseUrl.host)
                .port(newBaseUrl.port)
                .build()
            
            request = request.newBuilder()
                .url(newUrl)
                .build()
        }
        
        return chain.proceed(request)
    }
}
