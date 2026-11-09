package com.govi.androidbedrock.core.datastore

import com.govi.androidbedrock.core.network.auth.AuthTokenProvider

/**
 * Production-ready [AuthTokenProvider] base implementation powered by [TokenDataStore].
 *
 * Applications can extend this class or inject it directly, implementing [refreshToken]
 * against their specific authentication API endpoint.
 */
abstract class DefaultAuthTokenProvider(
    private val tokenDataStore: TokenDataStore,
) : AuthTokenProvider {
    override suspend fun getAccessToken(): String? = tokenDataStore.getAccessToken()

    override suspend fun onAuthFailed() {
        tokenDataStore.clearTokens()
    }
}
