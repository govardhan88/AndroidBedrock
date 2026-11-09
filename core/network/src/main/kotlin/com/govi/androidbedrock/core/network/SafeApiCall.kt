package com.govi.androidbedrock.core.network

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
            NetworkResult.Error(
                code = response.code(),
                message = response.message(),
                errorBody = response.errorBody()?.string(),
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        NetworkResult.Exception(e)
    }
