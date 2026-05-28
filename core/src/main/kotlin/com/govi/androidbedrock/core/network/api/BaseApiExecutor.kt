package com.govi.androidbedrock.core.network.api

import com.govi.androidbedrock.core.network.ApiException
import com.govi.androidbedrock.core.network.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

/**
 * Base API execution class for handling common API operations
 */
open class BaseApiExecutor {

    /**
     * Safe single API call - wraps in ApiResult
     */
    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): ApiResult<T> {
        return try {
            ApiResult.Success(apiCall())
        } catch (e: ApiException) {
            ApiResult.Error(e)
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
    protected suspend fun <T> unsafeApiCall(
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
    protected fun <T> safeApiFlow(
        apiCall: suspend () -> T
    ): Flow<ApiResult<T>> = flow {
        try {
            emit(ApiResult.Loading)
            emit(ApiResult.Success(apiCall()))
        } catch (e: Exception) {
            emit(handleFlowException(e))
        }
    }

    /**
     * Unsafe Flow-based API call - directly throws exceptions
     */
    protected fun <T> unsafeApiFlow(
        apiCall: suspend () -> T
    ): Flow<T> = flow {
        emit(apiCall())
    }.catch { e ->
        throw wrapException(e)
    }

    private fun handleFlowException(e: Exception): ApiResult.Error {
        return ApiResult.Error(wrapException(e))
    }

    private fun wrapException(e: Throwable): ApiException {
        return when (e) {
            is ApiException -> e
            is HttpException -> ApiException.HttpException(
                code = e.code(),
                message = e.message ?: "HTTP Error",
                body = e.response()?.errorBody()?.string()
            )
            is IOException -> ApiException.NetworkException(
                message = e.message ?: "Network error occurred",
                cause = e
            )
            else -> ApiException.UnknownException(
                message = e.message ?: "Unknown error occurred",
                cause = e
            )
        }
    }

    protected fun <T, R> mapApiResult(
        result: ApiResult<T>,
        transform: (T) -> R
    ): ApiResult<R> {
        return when (result) {
            is ApiResult.Success -> ApiResult.Success(transform(result.data))
            is ApiResult.Error -> ApiResult.Error(result.exception)
            is ApiResult.Loading -> ApiResult.Loading
        }
    }
}
