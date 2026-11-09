package com.govi.androidbedrock.core.network

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class SafeApiCallTest {
    @Test
    fun safeApiCall_returnsSuccess_whenResponseIsSuccessfulWithBody() =
        runTest {
            val result = safeApiCall { Response.success("test-data") }
            assertTrue(result is NetworkResult.Success)
            assertEquals("test-data", (result as NetworkResult.Success).data)
        }

    @Test
    fun safeApiCall_returnsError_whenResponseIsSuccessfulWithNullBody() =
        runTest {
            val result = safeApiCall { Response.success<String?>(null) }
            assertTrue(result is NetworkResult.Error)
            assertEquals("Empty response body", (result as NetworkResult.Error).message)
        }

    @Test
    fun safeApiCall_returnsError_whenResponseIsHttpError() =
        runTest {
            val errorResponse =
                Response.error<String>(
                    404,
                    "Not Found".toResponseBody("text/plain".toMediaType()),
                )
            val result = safeApiCall { errorResponse }
            assertTrue(result is NetworkResult.Error)
            assertEquals(404, (result as NetworkResult.Error).code)
        }

    @Test
    fun safeApiCall_returnsException_whenIOExceptionThrown() =
        runTest {
            val exception = java.io.IOException("Network down")
            val result = safeApiCall<String> { throw exception }
            assertTrue(result is NetworkResult.Exception)
            assertEquals(exception, (result as NetworkResult.Exception).throwable)
        }

    @Test(expected = CancellationException::class)
    fun safeApiCall_rethrowsCancellationException() =
        runTest {
            safeApiCall<String> {
                throw CancellationException("Coroutine cancelled")
            }
        }
}
