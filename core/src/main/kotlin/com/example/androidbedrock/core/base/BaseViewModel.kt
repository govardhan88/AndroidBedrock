package com.example.androidbedrock.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidbedrock.core.network.ApiResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel with common functionality for all ViewModels
 * Handles loading, error states, and safe coroutine execution
 */
abstract class BaseViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    protected fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    protected fun setError(message: String?) {
        _error.value = message
    }

    protected fun clearError() {
        _error.value = null
    }

    /**
     * Safe execution of suspend function with loading and error handling
     */
    protected fun <T> launchSafe(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onStart: () -> Unit = {},
        onSuccess: (T) -> Unit = {},
        onError: (String) -> Unit = {},
        block: suspend () -> T
    ): Job {
        return viewModelScope.launch(dispatcher) {
            try {
                setLoading(true)
                clearError()
                onStart()
                val result = block()
                onSuccess(result)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unexpected error occurred"
                setError(errorMessage)
                onError(errorMessage)
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Safe execution of ApiResult-based function
     */
    protected fun <T> launchSafeApi(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onStart: () -> Unit = {},
        onSuccess: (T) -> Unit = {},
        onError: (String) -> Unit = {},
        block: suspend () -> ApiResult<T>
    ): Job {
        return viewModelScope.launch(dispatcher) {
            try {
                setLoading(true)
                clearError()
                onStart()
                when (val result = block()) {
                    is ApiResult.Success -> onSuccess(result.data)
                    is ApiResult.Error -> {
                        val errorMessage = result.exception.message ?: "Unknown error"
                        setError(errorMessage)
                        onError(errorMessage)
                    }
                    is ApiResult.Loading -> {} // Already handling loading state
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unexpected error occurred"
                setError(errorMessage)
                onError(errorMessage)
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Safe collection of Flow with loading and error handling
     */
    protected fun <T> collectSafe(
        flow: Flow<ApiResult<T>>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onStart: () -> Unit = {},
        onSuccess: (T) -> Unit = {},
        onError: (String) -> Unit = {}
    ): Job {
        return viewModelScope.launch(dispatcher) {
            try {
                onStart()
                flow.collect { result ->
                    when (result) {
                        is ApiResult.Success -> onSuccess(result.data)
                        is ApiResult.Error -> {
                            val errorMessage = result.exception.message ?: "Unknown error"
                            setError(errorMessage)
                            onError(errorMessage)
                        }
                        is ApiResult.Loading -> setLoading(true)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unexpected error occurred"
                setError(errorMessage)
                onError(errorMessage)
            } finally {
                setLoading(false)
            }
        }
    }
}
