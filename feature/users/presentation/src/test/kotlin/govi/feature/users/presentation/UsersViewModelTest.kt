package govi.feature.users.presentation

import govi.core.navigation.AppNavigator
import govi.core.navigation.NavigationIntent
import govi.core.ui.AsyncState
import govi.feature.users.domain.model.User
import govi.feature.users.domain.repository.UserRepository
import govi.feature.users.domain.usecase.GetUsersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsersViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private class FakeAppNavigator : AppNavigator {
        override val navigationChannel: Flow<NavigationIntent> = emptyFlow()
        var lastNavigatedRoute: Any? = null
        var backNavigated: Boolean = false

        override fun navigateTo(
            route: Any,
            popUpToRoute: Any?,
            inclusive: Boolean,
            isSingleTop: Boolean,
        ) {
            lastNavigatedRoute = route
        }

        override fun navigateBack() {
            backNavigated = true
        }

        override fun popTo(
            route: Any,
            inclusive: Boolean,
        ) {}
    }

    private class FakeUserRepository(
        var result: Result<List<User>>,
    ) : UserRepository {
        override suspend fun getUsers(): Result<List<User>> = result

        override suspend fun getUserById(id: String): Result<User> = throw UnsupportedOperationException()
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsUsersSuccessfully() =
        runTest(testDispatcher) {
            val expectedUsers = listOf(User("1", "Alice", "alice@example.com"))
            val repository = FakeUserRepository(Result.success(expectedUsers))
            val useCase = GetUsersUseCase(repository)
            val navigator = FakeAppNavigator()

            val viewModel = UsersViewModel(useCase, navigator)
            assertEquals(AsyncState.Loading, viewModel.uiState.value.usersState)

            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.usersState is AsyncState.Success)
            assertEquals(expectedUsers, (viewModel.uiState.value.usersState as AsyncState.Success).data)
        }

    @Test
    fun init_emitsErrorState_whenLoadingFails() =
        runTest(testDispatcher) {
            val repository = FakeUserRepository(Result.failure(RuntimeException("Server unavailable")))
            val useCase = GetUsersUseCase(repository)
            val navigator = FakeAppNavigator()

            val viewModel = UsersViewModel(useCase, navigator)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.usersState is AsyncState.Error)
            assertEquals("Server unavailable", (viewModel.uiState.value.usersState as AsyncState.Error).message)
        }

    @Test
    fun processIntent_LoadUsers_reloadsData() =
        runTest(testDispatcher) {
            val repository = FakeUserRepository(Result.failure(RuntimeException("Initial failure")))
            val useCase = GetUsersUseCase(repository)
            val navigator = FakeAppNavigator()

            val viewModel = UsersViewModel(useCase, navigator)
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.usersState is AsyncState.Error)

            // Update repository to succeed and dispatch LoadUsers intent
            val expectedUsers = listOf(User("2", "Bob", "bob@example.com"))
            repository.result = Result.success(expectedUsers)
            viewModel.processIntent(UsersIntent.LoadUsers)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.usersState is AsyncState.Success)
            assertEquals(expectedUsers, (viewModel.uiState.value.usersState as AsyncState.Success).data)
        }
}
