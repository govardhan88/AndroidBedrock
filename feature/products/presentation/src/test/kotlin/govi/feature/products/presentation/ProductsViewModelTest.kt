package govi.feature.products.presentation

import govi.core.navigation.AppNavigator
import govi.core.navigation.NavigationIntent
import govi.core.ui.AsyncState
import govi.feature.products.domain.model.ProductsItem
import govi.feature.products.domain.repository.ProductsRepository
import govi.feature.products.domain.usecase.GetProductsItemsUseCase
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
class ProductsViewModelTest {
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

    private class FakeProductsRepository(
        var result: Result<List<ProductsItem>>,
    ) : ProductsRepository {
        override suspend fun getItems(): Result<List<ProductsItem>> = result

        override suspend fun getItemById(id: String): Result<ProductsItem> = throw UnsupportedOperationException()
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
    fun init_loadsProductsSuccessfully() =
        runTest(testDispatcher) {
            val expectedItems = listOf(ProductsItem("1", "Laptop", "Electronics"))
            val repository = FakeProductsRepository(Result.success(expectedItems))
            val useCase = GetProductsItemsUseCase(repository)
            val navigator = FakeAppNavigator()

            val viewModel = ProductsViewModel(useCase, navigator)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.itemsState is AsyncState.Success)
            assertEquals(expectedItems, (viewModel.uiState.value.itemsState as AsyncState.Success).data)
        }

    @Test
    fun onBackClicked_triggersNavigatorNavigateBack() =
        runTest(testDispatcher) {
            val repository = FakeProductsRepository(Result.success(emptyList()))
            val useCase = GetProductsItemsUseCase(repository)
            val navigator = FakeAppNavigator()

            val viewModel = ProductsViewModel(useCase, navigator)
            viewModel.processIntent(ProductsIntent.BackClicked)

            assertTrue(navigator.backNavigated)
        }
}
