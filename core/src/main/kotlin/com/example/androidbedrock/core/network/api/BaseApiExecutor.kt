package com.example.androidbedrock.core.network.api

import com.example.androidbedrock.core.network.ApiException
import com.example.androidbedrock.core.network.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

/**
 * Base API execution class for handling common API operations
 * Supports both single calls and Flow-based calls with unified isSafe pattern
 * 
 * Usage:
 *   - Safe (default): executeApi { apiCall() } // wraps in ApiResult
 *   - Unsafe: executeApi(isSafe = false) { apiCall() } // throws exceptions
 *   - Safe Flow: executeApiFlow { apiCall() } // emits ApiResult
 *   - Unsafe Flow: executeApiFlow(isSafe = false) { apiCall() } // throws exceptions
 */
open class BaseApiExecutor {

    /**
     * Execute single API call with unified error handling
     * @param isSafe If true (default), wraps errors in ApiResult. If false, throws exceptions
     * @param apiCall The API call to execute
     * @return ApiResult<T> if isSafe is true, otherwise T (or throws)
     */
    protected suspend fun <T> executeApi(
        isSafe: Boolean = true,
        apiCall: suspend () -> T
    ): Any {
        return if (isSafe) {
            executeSafeApiCall(apiCall)
        } else {
            executeUnsafeApiCall(apiCall)
        }
    }

    /**
     * Execute Flow-based API call with unified error handling
     * @param isSafe If true (default), wraps errors in ApiResult. If false, throws exceptions
     * @param apiCall The API call to execute
     * @return Flow<ApiResult<T>> if isSafe is true, Flow<T> if false
     */
    protected fun <T> executeApiFlow(
        isSafe: Boolean = true,
        apiCall: suspend () -> T
    ): Flow<Any> {
        return if (isSafe) {
            executeSafeApiFlow(apiCall)
        } else {
            executeUnsafeApiFlow(apiCall)
        }
    }

    /**
     * Safe single API call - wraps in ApiResult
     */
    private suspend fun <T> executeSafeApiCall(
        apiCall: suspend () -> T
    ): ApiResult<T> {
        return try {
            val result = apiCall()
            ApiResult.Success(result)
        } catch (e: HttpException) {
            ApiResult.Error(
                ApiException.HttpException(
                    code = e.code(),
                    message = e.message ?: "HTTP Error",
                    body = e.response()?.errorBody()?.string()
                )
            )
        } catch (e: IOException) {
            ApiResult.Error(
                ApiException.NetworkException(
                    message = e.message ?: "Network error occurred",
                    cause = e
                )
            )
        } catch (e: Exception) {
            ApiResult.Error(
                ApiException.UnknownException(
                    message = e.message ?: "Unknown error occurred",
                    cause = e
                )
            )
        }
    }

    /**
     * Unsafe single API call - directly returns data or throws
     */
    private suspend fun <T> executeUnsafeApiCall(
        apiCall: suspend () -> T
    ): T {
        return try {
            apiCall()
        } catch (e: HttpException) {
            throw ApiException.HttpException(
                code = e.code(),
                message = e.message ?: "HTTP Error",
                body = e.response()?.errorBody()?.string()
            )
        } catch (e: IOException) {
            throw ApiException.NetworkException(
                message = e.message ?: "Network error occurred",
                cause = e
            )
        }
    }

    /**
     * Safe Flow-based API call
     */
    private fun <T> executeSafeApiFlow(
        apiCall: suspend () -> T
    ): Flow<ApiResult<T>> = flow {
        try {
            emit(ApiResult.Loading())
            val result = apiCall()
            emit(ApiResult.Success(result))
        } catch (e: HttpException) {
            emit(
                ApiResult.Error(
                    ApiException.HttpException(
                        code = e.code(),
                        message = e.message ?: "HTTP Error",
                        body = e.response()?.errorBody()?.string()
                    )
                )
            )
        } catch (e: IOException) {
            emit(
                ApiResult.Error(
                    ApiException.NetworkException(
                        message = e.message ?: "Network error occurred",
                        cause = e
                    )
                )
            )
        } catch (e: Exception) {
            emit(
                ApiResult.Error(
                    ApiException.UnknownException(
                        message = e.message ?: "Unknown error occurred",
                        cause = e
                    )
                )
            )
        }
    }

    /**
     * Unsafe Flow-based API call - directly throws exceptions
     */
    private fun <T> executeUnsafeApiFlow(
        apiCall: suspend () -> T
    ): Flow<T> = flow {
        emit(apiCall())
    }.catch { e ->
        if (e is ApiException) {
            throw e
        } else if (e is HttpException) {
            throw ApiException.HttpException(
                code = e.code(),
                message = e.message ?: "HTTP Error",
                body = e.response()?.errorBody()?.string()
            )
        } else if (e is IOException) {
            throw ApiException.NetworkException(
                message = e.message ?: "Network error occurred",
                cause = e
            )
        } else {
            throw ApiException.UnknownException(
                message = e.message ?: "Unknown error occurred",
                cause = e
            )
        }
    }

    /**
     * Transform single result
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

    /**
     * Combine multiple API results
     */
    protected fun <T1, T2, R> combineApiResults(
        result1: ApiResult<T1>,
        result2: ApiResult<T2>,
        transform: (T1, T2) -> R
    ): ApiResult<R> {
        return when {
            result1 is ApiResult.Success && result2 is ApiResult.Success -> {
                ApiResult.Success(transform(result1.data, result2.data))
            }
            result1 is ApiResult.Error -> result1 as ApiResult<R>
            result2 is ApiResult.Error -> result2 as ApiResult<R>
            else -> ApiResult.Loading()
        }
    }
}
