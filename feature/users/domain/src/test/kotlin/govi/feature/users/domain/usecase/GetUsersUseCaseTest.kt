package govi.feature.users.domain.usecase

import govi.feature.users.domain.model.User
import govi.feature.users.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetUsersUseCaseTest {
    private class FakeUserRepository(
        private val result: Result<List<User>>,
    ) : UserRepository {
        override suspend fun getUsers(): Result<List<User>> = result

        override suspend fun getUserById(id: String): Result<User> = throw UnsupportedOperationException()
    }

    @Test
    fun getUsersUseCase_returnsSuccess_whenRepositorySucceeds() =
        runTest {
            val expectedUsers = listOf(User("1", "Alice", "alice@example.com"))
            val repository = FakeUserRepository(Result.success(expectedUsers))
            val useCase = GetUsersUseCase(repository)

            val result = useCase()
            assertTrue(result.isSuccess)
            assertEquals(expectedUsers, result.getOrNull())
        }

    @Test
    fun getUsersUseCase_returnsFailure_whenRepositoryFails() =
        runTest {
            val expectedException = RuntimeException("Network error")
            val repository = FakeUserRepository(Result.failure(expectedException))
            val useCase = GetUsersUseCase(repository)

            val result = useCase()
            assertTrue(result.isFailure)
            assertEquals(expectedException, result.exceptionOrNull())
        }
}
