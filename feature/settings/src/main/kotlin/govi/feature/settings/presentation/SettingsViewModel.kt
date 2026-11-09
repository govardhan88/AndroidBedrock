package govi.feature.settings.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import govi.core.base.viewmodel.BaseViewModel
import govi.core.navigation.AppNavigator
import govi.core.ui.AsyncState
import govi.feature.settings.domain.usecase.GetSettingsItemsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getItemsUseCase: GetSettingsItemsUseCase,
    private val appNavigator: AppNavigator,
) : BaseViewModel<SettingsUiState, SettingsIntent, SettingsEffect>(SettingsUiState()) {
    init {
        loadItems()
    }

    override fun processIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.LoadItems -> loadItems()
            is SettingsIntent.BackClicked -> onBackClicked()
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
