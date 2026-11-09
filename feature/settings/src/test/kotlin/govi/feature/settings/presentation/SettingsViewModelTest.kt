package govi.feature.settings.presentation

import govi.core.navigation.AppNavigator
import govi.core.navigation.NavigationIntent
import govi.core.ui.AsyncState
import govi.feature.settings.domain.model.SettingsItem
import govi.feature.settings.domain.repository.SettingsRepository
import govi.feature.settings.domain.usecase.GetSettingsItemsUseCase
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
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private class FakeAppNavigator : AppNavigator {
        override val navigationChannel: Flow<NavigationIntent> = emptyFlow()
        var backNavigated: Boolean = false

        override fun navigateTo(
            route: Any,
            popUpToRoute: Any?,
            inclusive: Boolean,
            isSingleTop: Boolean,
        ) {}

        override fun navigateBack() {
            backNavigated = true
        }

        override fun popTo(
            route: Any,
            inclusive: Boolean,
        ) {}
    }

    private class FakeSettingsRepository(
        var result: Result<List<SettingsItem>>,
    ) : SettingsRepository {
        override suspend fun getItems(): Result<List<SettingsItem>> = result

        override suspend fun getItemById(id: String): Result<SettingsItem> = throw UnsupportedOperationException()
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
    fun init_loadsSettingsSuccessfully() =
        runTest(testDispatcher) {
            val expectedItems = listOf(SettingsItem("1", "Dark Mode", "Theme setting"))
            val repository = FakeSettingsRepository(Result.success(expectedItems))
            val useCase = GetSettingsItemsUseCase(repository)
            val navigator = FakeAppNavigator()

            val viewModel = SettingsViewModel(useCase, navigator)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.itemsState is AsyncState.Success)
            assertEquals(expectedItems, (viewModel.uiState.value.itemsState as AsyncState.Success).data)
        }

    @Test
    fun onBackClicked_triggersNavigatorNavigateBack() =
        runTest(testDispatcher) {
            val repository = FakeSettingsRepository(Result.success(emptyList()))
            val useCase = GetSettingsItemsUseCase(repository)
            val navigator = FakeAppNavigator()

            val viewModel = SettingsViewModel(useCase, navigator)
            viewModel.processIntent(SettingsIntent.BackClicked)

            assertTrue(navigator.backNavigated)
        }
}
