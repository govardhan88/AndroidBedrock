package com.govi.androidbedrock.core.network

import com.govi.androidbedrock.core.utils.NetworkConstants
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for providing the base URL of the API.
 * This allows for dynamic base URL switching (e.g., for different environments or regions).
 */
interface BaseUrlProvider {
    fun getBaseUrl(): String
}

/**
 * Default implementation of BaseUrlProvider.
 * In a real app, this might read from BuildConfig, SharedPreferences, or a remote config.
 */
@Singleton
class DefaultBaseUrlProvider @Inject constructor() : BaseUrlProvider {
    
    private var currentUrl: String = NetworkConstants.DEFAULT_BASE_URL

    override fun getBaseUrl(): String = currentUrl

    /**
     * Updates the base URL at runtime.
     * Note: Retrofit instances are typically immutable. 
     * To truly support dynamic switching, you might need a dynamic interceptor 
     * or to recreate the Retrofit instance.
     */
    fun setBaseUrl(url: String) {
        currentUrl = if (url.endsWith("/")) url else "$url/"
    }
}
