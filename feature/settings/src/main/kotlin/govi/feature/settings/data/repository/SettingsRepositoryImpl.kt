package govi.feature.settings.data.repository

import govi.core.network.NetworkResult
import govi.core.network.safeApiCall
import govi.feature.settings.data.remote.SettingsApiService
import govi.feature.settings.domain.model.SettingsItem
import govi.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SettingsRepositoryImpl
    @Inject
    constructor(
        private val apiService: SettingsApiService,
    ) : SettingsRepository {
        override suspend fun getItems(): Result<List<SettingsItem>> =
            when (val result = safeApiCall { apiService.getItems() }) {
                is NetworkResult.Success -> Result.success(result.data.map { it.toDomain() })
                is NetworkResult.Error -> Result.failure(Exception("HTTP ${result.code}: ${result.message}"))
                is NetworkResult.Exception -> Result.failure(result.throwable)
            }

        override suspend fun getItemById(id: String): Result<SettingsItem> =
            when (val result = safeApiCall { apiService.getItemById(id) }) {
                is NetworkResult.Success -> Result.success(result.data.toDomain())
                is NetworkResult.Error -> Result.failure(Exception("HTTP ${result.code}: ${result.message}"))
                is NetworkResult.Exception -> Result.failure(result.throwable)
            }
    }
