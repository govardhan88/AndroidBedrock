package govi.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenDataStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : TokenDataStore {
    override val accessTokenFlow: Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[KEY_ACCESS_TOKEN]
        }

    override val refreshTokenFlow: Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[KEY_REFRESH_TOKEN]
        }

    override suspend fun getAccessToken(): String? = accessTokenFlow.firstOrNull()

    override suspend fun getRefreshToken(): String? = refreshTokenFlow.firstOrNull()

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
            preferences[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences.remove(KEY_REFRESH_TOKEN)
        }
    }

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}
