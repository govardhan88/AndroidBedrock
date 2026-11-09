package com.govi.androidbedrock.core.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govi.androidbedrock.core.base.mvi.UiEffect
import com.govi.androidbedrock.core.base.mvi.UiIntent
import com.govi.androidbedrock.core.base.mvi.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel adhering to Unidirectional Data Flow (MVI/MVVM).
 *
 * @param State The immutable UI state type.
 * @param Intent The user action/intent type.
 * @param Effect The one-time event/effect type.
 */
abstract class BaseViewModel<State : UiState, Intent : UiIntent, Effect : UiEffect>(
    initialState: State,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    private val _effect = Channel<Effect>(Channel.BUFFERED)
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    val currentState: State
        get() = _uiState.value

    /**
     * Handle incoming user actions/intents.
     */
    abstract fun processIntent(intent: Intent)

    /**
     * Safely update the current UI state.
     */
    protected fun updateState(reducer: State.() -> State) {
        _uiState.update(reducer)
    }

    /**
     * Emit a single-shot side effect (navigation, snackbar, toast).
     */
    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
