package govi.core.network

import retrofit2.Response

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
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
                errorBody = response.errorBody()?.string()
            )
        }
    } catch (e: Throwable) {
        NetworkResult.Exception(e)
    }
}
