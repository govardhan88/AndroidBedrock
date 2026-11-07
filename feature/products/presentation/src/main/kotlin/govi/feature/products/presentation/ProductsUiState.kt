package govi.feature.products.presentation

import govi.core.ui.AsyncState
import govi.feature.products.domain.model.ProductsItem

data class ProductsUiState(
    val itemsState: AsyncState<List<ProductsItem>> = AsyncState.Loading,
)
