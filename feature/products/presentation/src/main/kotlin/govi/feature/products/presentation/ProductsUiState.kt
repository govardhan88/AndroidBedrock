package govi.feature.products.presentation

import govi.core.base.mvi.UiEffect
import govi.core.base.mvi.UiIntent
import govi.core.base.mvi.UiState
import govi.core.ui.AsyncState
import govi.feature.products.domain.model.ProductsItem

data class ProductsUiState(
    val itemsState: AsyncState<List<ProductsItem>> = AsyncState.Loading,
) : UiState

sealed interface ProductsIntent : UiIntent {
    data object LoadItems : ProductsIntent

    data object BackClicked : ProductsIntent
}

sealed interface ProductsEffect : UiEffect {
    data object NavigateBack : ProductsEffect
}
