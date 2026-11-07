package govi.feature.users.data.repository

import govi.core.network.NetworkResult
import govi.core.network.safeApiCall
import govi.feature.users.data.remote.UserApiService
import govi.feature.users.domain.model.User
import govi.feature.users.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl
    @Inject
    constructor(
        private val apiService: UserApiService,
    ) : UserRepository {
        override suspend fun getUsers(): Result<List<User>> =
            when (val result = safeApiCall { apiService.getUsers() }) {
                is NetworkResult.Success -> Result.success(result.data.map { it.toDomain() })
                is NetworkResult.Error -> Result.failure(Exception("HTTP ${result.code}: ${result.message}"))
                is NetworkResult.Exception -> Result.failure(result.throwable)
            }

        override suspend fun getUserById(id: String): Result<User> =
            when (val result = safeApiCall { apiService.getUserById(id) }) {
                is NetworkResult.Success -> Result.success(result.data.toDomain())
                is NetworkResult.Error -> Result.failure(Exception("HTTP ${result.code}: ${result.message}"))
                is NetworkResult.Exception -> Result.failure(result.throwable)
            }
    }
