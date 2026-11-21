package com.govi.androidbedrock.core.network

import com.govi.androidbedrock.core.base.result.AppResult
import com.govi.androidbedrock.core.network.error.HttpErrorMessages
import com.govi.androidbedrock.core.network.error.toAppResult
import kotlinx.coroutines.CancellationException
import retrofit2.Response

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> =
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(response.code(), "Empty response body", null)
            }
        } else {
            val rawMsg = response.message()
            val fallbackMsg =
                if (rawMsg.isNullOrBlank()) {
                    HttpErrorMessages.getMessage(response.code())
                } else {
                    rawMsg
                }
            NetworkResult.Error(
                code = response.code(),
                message = fallbackMsg,
                errorBody = response.errorBody()?.string(),
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        NetworkResult.Exception(e)
    }

/**
 * Directly executes a Retrofit API call and transforms the result into a domain [AppResult],
 * automatically mapping HTTP status codes and network exceptions into categorized [AppError]s.
 */
suspend fun <T> safeApiResultCall(apiCall: suspend () -> Response<T>): AppResult<T> = safeApiCall(apiCall).toAppResult()
