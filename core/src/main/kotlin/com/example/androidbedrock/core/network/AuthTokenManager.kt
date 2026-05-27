package com.example.androidbedrock.core.network

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages authentication tokens with automatic refresh and lifecycle management
 */
@Singleton
class AuthTokenManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_ACCESS_TOKEN = "auth_access_token"
        private const val KEY_REFRESH_TOKEN = "auth_refresh_token"
        private const val KEY_TOKEN_EXPIRY = "auth_token_expiry"
    }

    /**
     * Get current access token
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Set access token with optional expiry time
     */
    fun setAccessToken(token: String, expiryMs: Long? = null) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, token)
            if (expiryMs != null) {
                putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + expiryMs)
            }
            apply()
        }
    }

    /**
     * Get refresh token
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
    }

    /**
     * Set refresh token
     */
    fun setRefreshToken(token: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    /**
     * Check if token is expired
     */
    fun isTokenExpired(): Boolean {
        val expiry = sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
        return expiry > 0 && System.currentTimeMillis() > expiry
    }

    /**
     * Clear all tokens
     */
    fun clearTokens() {
        sharedPreferences.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_EXPIRY)
            apply()
        }
    }

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }
}
