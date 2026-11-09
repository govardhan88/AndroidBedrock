package govi.feature.products.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import govi.core.navigation.AppNavigator
import govi.core.ui.AsyncState
import govi.feature.products.domain.usecase.GetProductsItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getItemsUseCase: GetProductsItemsUseCase,
    private val appNavigator: AppNavigator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(itemsState = AsyncState.Loading) }
            getItemsUseCase().fold(
                onSuccess = { items ->
                    _uiState.update { it.copy(itemsState = AsyncState.Success(items)) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
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
