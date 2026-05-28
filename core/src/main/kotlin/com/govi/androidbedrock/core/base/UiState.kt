package com.govi.androidbedrock.core.base

import com.govi.androidbedrock.core.network.ApiResult
import com.govi.androidbedrock.core.utils.DispatchersProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

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

/**
 * Mapper extension to convert ApiResult to UiState
 */
fun <T> ApiResult<T>.toUiState(): UiState<T> {
    return when (this) {
        is ApiResult.Success -> UiState(data = data, isLoading = false, error = null)
        is ApiResult.Error -> UiState(data = null, isLoading = false, error = exception.message)
        is ApiResult.Loading -> UiState(data = null, isLoading = true, error = null)
    }
}

/**
 * Extension to convert a Flow of ApiResult to a Flow of UiState with dispatcher switching
 */
fun <T> Flow<ApiResult<T>>.asUiStateFlow(
    dispatchers: DispatchersProvider
): Flow<UiState<T>> {
    return this
        .map { it.toUiState() }
        .onStart { emit(UiState(isLoading = true)) }
        // Ensure the upstream flow runs on the IO dispatcher
        .flowOn(dispatchers.io())
}

/**
 * Safely execute a block on the Main dispatcher
 */
suspend fun <T> withMain(
    dispatchers: DispatchersProvider,
    block: suspend () -> T
): T = withContext(dispatchers.main()) {
    block()
}

/**
 * Safely execute a block on the IO dispatcher
 */
suspend fun <T> withIO(
    dispatchers: DispatchersProvider,
    block: suspend () -> T
): T = withContext(dispatchers.io()) {
    block()
}
