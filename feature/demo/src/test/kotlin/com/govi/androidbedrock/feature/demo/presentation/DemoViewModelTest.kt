package com.govi.androidbedrock.feature.demo.presentation

import com.govi.androidbedrock.core.navigation.AppNavigator
import com.govi.androidbedrock.core.navigation.NavigationIntent
import com.govi.androidbedrock.core.navigation.route.AppRoute
import com.govi.androidbedrock.core.ui.AsyncState
import com.govi.androidbedrock.feature.demo.data.repository.DemoRepositoryImpl
import com.govi.androidbedrock.feature.demo.domain.usecase.GetDemoItemsUseCase
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
class DemoViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var navigator: TestAppNavigator
    private lateinit var repository: DemoRepositoryImpl
    private lateinit var getDemoItemsUseCase: GetDemoItemsUseCase
    private lateinit var viewModel: DemoViewModel

    class TestAppNavigator : AppNavigator {
        override val navigationChannel: Flow<NavigationIntent> = emptyFlow()
        var lastNavigatedRoute: Any? = null
        var backPressed: Boolean = false

        override fun navigateTo(
            route: Any,
            popUpToRoute: Any?,
            inclusive: Boolean,
            isSingleTop: Boolean,
        ) {
            lastNavigatedRoute = route
        }

        override fun navigateBack() {
            backPressed = true
        }

        override fun popTo(
            route: Any,
            inclusive: Boolean,
        ) {
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        navigator = TestAppNavigator()
        repository = DemoRepositoryImpl()
        getDemoItemsUseCase = GetDemoItemsUseCase(repository)
        viewModel = DemoViewModel(getDemoItemsUseCase, navigator)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization loads demo items into Success state`() =
        runTest {
            advanceUntilIdle()
            val state = viewModel.uiState.value
            assertTrue(state.itemsState is AsyncState.Success)
            val items = (state.itemsState as AsyncState.Success).data
            assertEquals(6, items.size)
        }

    @Test
    fun `LoadItems intent reloads demo items`() =
        runTest {
            viewModel.processIntent(DemoIntent.LoadItems)
            advanceUntilIdle()
            val state = viewModel.uiState.value
            assertTrue(state.itemsState is AsyncState.Success)
        }

    @Test
    fun `failure in repository updates state to Error`() =
        runTest {
            val failingRepo =
                object : com.govi.androidbedrock.feature.demo.domain.repository.DemoRepository {
                    override suspend fun getItems() =
                        Result.failure<List<com.govi.androidbedrock.feature.demo.domain.model.DemoItem>>(
                            RuntimeException("Repository failure"),
                        )

                    override suspend fun getItemById(id: String) =
                        Result.failure<com.govi.androidbedrock.feature.demo.domain.model.DemoItem>(
                            RuntimeException("Not found"),
                        )
                }
            val failingViewModel = DemoViewModel(GetDemoItemsUseCase(failingRepo), navigator)
            advanceUntilIdle()
            val state = failingViewModel.uiState.value
            assertTrue(state.itemsState is AsyncState.Error)
            assertEquals("Repository failure", (state.itemsState as AsyncState.Error).message)
        }

    @Test
    fun `OptionClicked intent navigates to DemoDetail route`() =
        runTest {
            viewModel.processIntent(DemoIntent.OptionClicked("ui-components"))
            assertEquals(AppRoute.DemoDetail("ui-components"), navigator.lastNavigatedRoute)
        }

    @Test
    fun `BackClicked intent calls navigateBack`() =
        runTest {
            viewModel.processIntent(DemoIntent.BackClicked)
            assertTrue(navigator.backPressed)
        }
}
