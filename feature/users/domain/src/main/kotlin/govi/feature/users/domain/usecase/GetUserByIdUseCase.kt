package govi.feature.users.domain.usecase

import govi.feature.users.domain.model.User
import govi.feature.users.domain.repository.UserRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: String): Result<User> {
        return userRepository.getUserById(id)
    }
}
