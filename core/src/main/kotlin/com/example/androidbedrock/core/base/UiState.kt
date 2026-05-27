package com.example.androidbedrock.core.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * Base state holder for ViewModel state management
 */
data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isSuccess: Boolean
        get() = data != null && error == null && !isLoading

    val isEmpty: Boolean
        get() = data == null && !isLoading && error == null
}

/**
 * Base interface for UI state management
 */
interface UiStateHolder<T> {
    val state: StateFlow<UiState<T>>
}
