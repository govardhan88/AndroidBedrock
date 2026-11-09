package com.govi.androidbedrock.core.base.result

/**
 * Standard functional Result monad for Clean Architecture operations.
 */
sealed interface AppResult<out T> {
    data class Success<out T>(
        val data: T,
    ) : AppResult<T>

    data class Error(
        val error: AppError,
    ) : AppResult<Nothing>

    data object Loading : AppResult<Nothing>

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data

    fun <R> map(transform: (T) -> R): AppResult<R> =
        when (this) {
            is Success -> Success(transform(data))
            is Error -> this
            Loading -> Loading
        }

    fun onSuccess(action: (T) -> Unit): AppResult<T> {
        if (this is Success) action(data)
        return this
    }

    fun onError(action: (AppError) -> Unit): AppResult<T> {
        if (this is Error) action(error)
        return this
    }
}
