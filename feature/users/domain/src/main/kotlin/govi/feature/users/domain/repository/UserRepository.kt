package govi.feature.users.domain.repository

import govi.feature.users.domain.model.User

interface UserRepository {
    suspend fun getUsers(): Result<List<User>>

    suspend fun getUserById(id: String): Result<User>
}
