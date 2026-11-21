package com.govi.androidbedrock.feature.demo.presentation

import androidx.lifecycle.viewModelScope
import com.govi.androidbedrock.core.base.viewmodel.BaseViewModel
import com.govi.androidbedrock.core.navigation.AppNavigator
import com.govi.androidbedrock.core.navigation.route.AppRoute
import com.govi.androidbedrock.core.ui.AsyncState
import com.govi.androidbedrock.feature.demo.domain.usecase.GetDemoItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DemoViewModel @Inject constructor(
    private val getItemsUseCase: GetDemoItemsUseCase,
    private val appNavigator: AppNavigator,
) : BaseViewModel<DemoUiState, DemoIntent, DemoEffect>(DemoUiState()) {
    init {
        loadItems()
    }

    override fun processIntent(intent: DemoIntent) {
        when (intent) {
            is DemoIntent.LoadItems -> loadItems()
            is DemoIntent.OptionClicked -> onOptionClicked(intent.optionId)
            is DemoIntent.BackClicked -> onBackClicked()
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

    fun onOptionClicked(optionId: String) {
        appNavigator.navigateTo(AppRoute.DemoDetail(optionId = optionId))
    }

    fun onBackClicked() {
        appNavigator.navigateBack()
    }
}
