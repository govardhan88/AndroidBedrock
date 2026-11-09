package govi.feature.fixture.presentation

import govi.core.base.mvi.UiEffect
import govi.core.base.mvi.UiIntent
import govi.core.base.mvi.UiState
import govi.core.ui.AsyncState
import govi.feature.fixture.domain.model.FixtureItem

data class FixtureUiState(
    val itemsState: AsyncState<List<FixtureItem>> = AsyncState.Loading,
) : UiState

sealed interface FixtureIntent : UiIntent {
    data object LoadItems : FixtureIntent

    data object BackClicked : FixtureIntent
}

sealed interface FixtureEffect : UiEffect {
    data object NavigateBack : FixtureEffect
}
