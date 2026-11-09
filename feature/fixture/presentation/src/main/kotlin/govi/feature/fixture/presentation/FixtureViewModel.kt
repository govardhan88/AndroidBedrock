package govi.feature.fixture.presentation

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import govi.core.base.viewmodel.BaseViewModel
import govi.core.navigation.AppNavigator
import govi.core.ui.AsyncState
import govi.feature.fixture.domain.usecase.GetFixtureItemsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FixtureViewModel @Inject constructor(
    private val getItemsUseCase: GetFixtureItemsUseCase,
    private val appNavigator: AppNavigator,
) : BaseViewModel<FixtureUiState, FixtureIntent, FixtureEffect>(FixtureUiState()) {
    init {
        loadItems()
    }

    override fun processIntent(intent: FixtureIntent) {
        when (intent) {
            is FixtureIntent.LoadItems -> loadItems()
            is FixtureIntent.BackClicked -> onBackClicked()
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
