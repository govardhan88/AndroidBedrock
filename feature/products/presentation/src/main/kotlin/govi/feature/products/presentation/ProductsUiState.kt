package govi.feature.products.presentation

import govi.feature.products.domain.model.ProductsItem

sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Success(val items: List<ProductsItem>) : ProductsUiState
    data class Error(val message: String) : ProductsUiState
}
