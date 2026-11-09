package com.govi.androidbedrock.core.datastore

import kotlinx.coroutines.flow.Flow

/**
 * Contract for persistent storage of authentication tokens.
 *
 * Implemented using Android Jetpack DataStore Preferences for non-blocking,
 * asynchronous, and transaction-safe token caching.
 */
interface TokenDataStore {
    val accessTokenFlow: Flow<String?>
    val refreshTokenFlow: Flow<String?>

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    )

    suspend fun clearTokens()
}
