package govi.feature.settings.presentation

import govi.core.base.mvi.UiEffect
import govi.core.base.mvi.UiIntent
import govi.core.base.mvi.UiState
import govi.core.ui.AsyncState
import govi.feature.settings.domain.model.SettingsItem

data class SettingsUiState(
    val itemsState: AsyncState<List<SettingsItem>> = AsyncState.Loading,
) : UiState

sealed interface SettingsIntent : UiIntent {
    data object LoadItems : SettingsIntent

    data object BackClicked : SettingsIntent
}

sealed interface SettingsEffect : UiEffect {
    data object NavigateBack : SettingsEffect
}
