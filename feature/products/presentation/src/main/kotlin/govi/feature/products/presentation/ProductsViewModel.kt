package govi.feature.products.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import govi.core.base.viewmodel.BaseViewModel
import govi.core.navigation.AppNavigator
import govi.core.ui.AsyncState
import govi.feature.products.domain.usecase.GetProductsItemsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getItemsUseCase: GetProductsItemsUseCase,
    private val appNavigator: AppNavigator,
) : BaseViewModel<ProductsUiState, ProductsIntent, ProductsEffect>(ProductsUiState()) {
    init {
        loadItems()
    }

    override fun processIntent(intent: ProductsIntent) {
        when (intent) {
            is ProductsIntent.LoadItems -> loadItems()
            is ProductsIntent.BackClicked -> onBackClicked()
        }
    }

    fun loadItems() {
        viewModelScope.launch {
            updateState { copy(itemsState = AsyncState.Loading) }
            getItemsUseCase().fold(
                onSuccess = { items ->
                    updateState { copy(itemsState = AsyncState.Success(items)) }
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            itemsState =
                                AsyncState.Error(
                                    error.message ?: "An unexpected error occurred",
                                ),
                        )
                    }
                },
            )
        }
    }

    fun onBackClicked() {
        appNavigator.navigateBack()
    }
}
