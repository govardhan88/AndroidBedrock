package com.example.androidbedrock.core.base

import com.example.androidbedrock.core.network.ApiException
import com.example.androidbedrock.core.network.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Base Repository for all repositories
 * Handles common API call logic with safe/unsafe execution patterns
 */
abstract class BaseRepository {

    /**
     * Safe API call - wraps result in ApiResult
     */
    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): ApiResult<T> {
        return try {
            val result = apiCall()
            ApiResult.Success(result)
        } catch (e: ApiException) {
            ApiResult.Error(e)
        } catch (e: Exception) {
            ApiResult.Error(
                ApiException.UnknownException(
                    e.message ?: "Unknown error occurred",
                    e
                )
            )
        }
    }

    /**
     * Unsafe API call - directly returns data or throws exception
     */
    protected suspend fun <T> unsafeApiCall(
        apiCall: suspend () -> T
    ): T {
        return apiCall()
    }

    /**
     * Safe Flow-based API call
     */
    protected fun <T> safeApiFlow(
        apiCall: suspend () -> T
    ): Flow<ApiResult<T>> = flow {
        try {
            emit(ApiResult.Loading())
            val result = apiCall()
            emit(ApiResult.Success(result))
        } catch (e: ApiException) {
            emit(ApiResult.Error(e))
        } catch (e: Exception) {
            emit(
                ApiResult.Error(
                    ApiException.UnknownException(
                        e.message ?: "Unknown error occurred",
                        e
                    )
                )
            )
        }
    }

    /**
     * Unsafe Flow-based API call - directly throws exceptions
     */
    protected fun <T> unsafeApiFlow(
        apiCall: suspend () -> T
    ): Flow<T> = flow {
        emit(apiCall())
    }

    /**
     * Transform ApiResult
     */
    protected fun <T, R> mapApiResult(
        result: ApiResult<T>,
        transform: (T) -> R
    ): ApiResult<R> {
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(transform(result.data))
            is ApiResult.Error -> ApiResult.Error(result.exception)
            is ApiResult.Loading -> ApiResult.Loading()
        }
    }
}
