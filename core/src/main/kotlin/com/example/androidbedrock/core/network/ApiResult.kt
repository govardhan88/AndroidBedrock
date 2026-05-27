package com.example.androidbedrock.core.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Sealed class for API response handling
 * Supports both single calls and Flow-based calls
 */
seal class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val exception: ApiException) : ApiResult<T>()
    class Loading<T> : ApiResult<T>()

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
        is Loading -> null
    }

    fun exceptionOrNull(): ApiException? = when (this) {
        is Success -> null
        is Error -> exception
        is Loading -> null
    }

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
}

/**
 * Safely execute a suspend function and wrap result in ApiResult
 */
suspend inline fun <T> safeApiCall(crossinline block: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(block())
    } catch (e: ApiException) {
        ApiResult.Error(e)
    } catch (e: Exception) {
        ApiResult.Error(ApiException.UnknownException(e.message ?: "Unknown error", e))
    }
}

/**
 * Safely execute a Flow-based API call and wrap result in ApiResult Flow
 */
fun <T> Flow<T>.safeApiFlow(): Flow<ApiResult<T>> {
    return this
        .map<T, ApiResult<T>> { ApiResult.Success(it) }
        .catch { exception ->
            val apiException = when (exception) {
                is ApiException -> exception
                else -> ApiException.UnknownException(exception.message ?: "Unknown error", exception)
            }
            emit(ApiResult.Error(apiException))
        }
}
