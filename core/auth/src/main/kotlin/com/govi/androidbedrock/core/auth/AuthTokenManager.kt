package com.govi.androidbedrock.core.auth

import android.content.SharedPreferences
import com.govi.androidbedrock.core.di.qualifier.SecureStorage
import com.govi.androidbedrock.core.utils.AuthConstants
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

/**
 * Manages authentication tokens with automatic refresh and lifecycle management
 */
@Singleton
class AuthTokenManager @Inject constructor(
    @SecureStorage private val sharedPreferences: SharedPreferences
) {

    /**
     * Get current access token
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(AuthConstants.KEY_ACCESS_TOKEN, null)
    }

    /**
     * Set access token with optional expiry time
     */
    fun setAccessToken(token: String, expiryMs: Long? = null) {
        sharedPreferences.edit().apply {
            putString(AuthConstants.KEY_ACCESS_TOKEN, token)
            if (expiryMs != null) {
                putLong(AuthConstants.KEY_TOKEN_EXPIRY, System.currentTimeMillis() + expiryMs)
            }
            apply()
        }
    }

    /**
     * Get refresh token
     */
    fun getRefreshToken(): String? {
        return sharedPreferences.getString(AuthConstants.KEY_REFRESH_TOKEN, null)
    }

    /**
     * Set refresh token
     */
    fun setRefreshToken(token: String) {
        sharedPreferences.edit { putString(AuthConstants.KEY_REFRESH_TOKEN, token) }
    }

    /**
     * Check if token is expired
     */
    fun isTokenExpired(): Boolean {
        val expiry = sharedPreferences.getLong(AuthConstants.KEY_TOKEN_EXPIRY, 0)
        return expiry > 0 && System.currentTimeMillis() > expiry
    }

    /**
     * Clear all tokens
     */
    fun clearTokens() {
        sharedPreferences.edit().apply {
            remove(AuthConstants.KEY_ACCESS_TOKEN)
            remove(AuthConstants.KEY_REFRESH_TOKEN)
            remove(AuthConstants.KEY_TOKEN_EXPIRY)
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
