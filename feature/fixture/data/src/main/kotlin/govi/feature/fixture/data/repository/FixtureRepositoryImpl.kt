package govi.feature.fixture.data.repository

import govi.core.network.NetworkResult
import govi.core.network.safeApiCall
import govi.feature.fixture.data.remote.FixtureApiService
import govi.feature.fixture.domain.model.FixtureItem
import govi.feature.fixture.domain.repository.FixtureRepository
import javax.inject.Inject

class FixtureRepositoryImpl @Inject constructor(
    private val apiService: FixtureApiService
) : FixtureRepository {

    override suspend fun getItems(): Result<List<FixtureItem>> {
        return when (val result = safeApiCall { apiService.getItems() }) {
            is NetworkResult.Success -> Result.success(result.data.map { it.toDomain() })
            is NetworkResult.Error -> Result.failure(Exception("HTTP ${result.code}: ${result.message}"))
            is NetworkResult.Exception -> Result.failure(result.throwable)
        }
    }

    override suspend fun getItemById(id: String): Result<FixtureItem> {
        return when (val result = safeApiCall { apiService.getItemById(id) }) {
            is NetworkResult.Success -> Result.success(result.data.toDomain())
            is NetworkResult.Error -> Result.failure(Exception("HTTP ${result.code}: ${result.message}"))
            is NetworkResult.Exception -> Result.failure(result.throwable)
        }
    }
}
