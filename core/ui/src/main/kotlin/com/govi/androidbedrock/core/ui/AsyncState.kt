package com.govi.androidbedrock.core.ui

/**
 * Standardized asynchronous state wrapper for LCE (Loading-Content-Error) data models.
 * Can be used directly as a screen UI state for simple CRUD screens,
 * or embedded inside a rich feature-specific UiState data class.
 */
sealed interface AsyncState<out T> {
    data object Idle : AsyncState<Nothing>

    data object Loading : AsyncState<Nothing>

    data class Success<out T>(
        val data: T,
    ) : AsyncState<T>

    data class Error(
        val message: String,
        val throwable: Throwable? = null,
    ) : AsyncState<Nothing>
}

/**
 * Returns data if state is [AsyncState.Success], or null otherwise.
 */
fun <T> AsyncState<T>.getOrNull(): T? = (this as? AsyncState.Success)?.data

val AsyncState<*>.isLoading: Boolean get() = this is AsyncState.Loading
val AsyncState<*>.isSuccess: Boolean get() = this is AsyncState.Success
val AsyncState<*>.isError: Boolean get() = this is AsyncState.Error
val AsyncState<*>.isIdle: Boolean get() = this is AsyncState.Idle
