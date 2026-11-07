package govi.feature.fixture.presentation

import govi.core.ui.AsyncState
import govi.feature.fixture.domain.model.FixtureItem

data class FixtureUiState(
    val itemsState: AsyncState<List<FixtureItem>> = AsyncState.Loading,
)
