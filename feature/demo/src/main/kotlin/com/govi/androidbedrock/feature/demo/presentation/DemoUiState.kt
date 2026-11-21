package com.govi.androidbedrock.feature.demo.presentation

import com.govi.androidbedrock.core.base.mvi.UiEffect
import com.govi.androidbedrock.core.base.mvi.UiIntent
import com.govi.androidbedrock.core.base.mvi.UiState
import com.govi.androidbedrock.core.ui.AsyncState
import com.govi.androidbedrock.feature.demo.domain.model.DemoItem

data class DemoUiState(
    val itemsState: AsyncState<List<DemoItem>> = AsyncState.Loading,
) : UiState

sealed interface DemoIntent : UiIntent {
    data object LoadItems : DemoIntent

    data class OptionClicked(
        val optionId: String,
    ) : DemoIntent

    data object BackClicked : DemoIntent
}

sealed interface DemoEffect : UiEffect {
    data class NavigateToDetail(
        val optionId: String,
    ) : DemoEffect

    data object NavigateBack : DemoEffect
}
