package govi.feature.settings.presentation

import govi.core.ui.AsyncState
import govi.feature.settings.domain.model.SettingsItem

data class SettingsUiState(
    val itemsState: AsyncState<List<SettingsItem>> = AsyncState.Loading,
)
