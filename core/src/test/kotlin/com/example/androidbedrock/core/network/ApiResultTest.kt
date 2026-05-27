package com.example.androidbedrock.core.network

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ApiResultTest {

    @Test
    fun testSuccessResult() {
        val result = ApiResult.Success("test data")
        assertTrue(result.isSuccess())
        assertFalse(result.isError())
        assertEquals("test data", result.getOrNull())
        assertNull(result.exceptionOrNull())
    }

    @Test
    fun testErrorResult() {
        val exception = ApiException.NetworkException("Network error")
        val result = ApiResult.Error<String>(exception)
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertNull(result.getOrNull())
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun testLoadingResult() {
        val result = ApiResult.Loading<String>()
        assertFalse(result.isSuccess())
        assertFalse(result.isError())
        assertTrue(result.isLoading())
    }

    @Test
    fun testSafeApiCall_Success() = runTest {
        val result = safeApiCall {
            "success"
        }
        assertTrue(result.isSuccess())
        assertEquals("success", (result as ApiResult.Success).data)
    }

    @Test
    fun testSafeApiCall_Error() = runTest {
        val result = safeApiCall {
            throw Exception("test error")
        }
        assertTrue(result.isError())
    }
}
