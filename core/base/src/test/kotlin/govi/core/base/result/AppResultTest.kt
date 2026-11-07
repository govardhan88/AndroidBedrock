package govi.core.base.result

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppResultTest {
    @Test
    fun `success result holds data and reports flags correctly`() {
        val result = AppResult.Success("test_data")

        assertTrue(result.isSuccess)
        assertFalse(result.isError)
        assertFalse(result.isLoading)
        assertEquals("test_data", result.data)
        assertEquals("test_data", result.getOrNull())
    }

    @Test
    fun `error result holds app error and reports flags correctly`() {
        val error = AppError.Network("Offline")
        val result = AppResult.Error(error)

        assertFalse(result.isSuccess)
        assertTrue(result.isError)
        assertFalse(result.isLoading)
        assertEquals(error, result.error)
        assertNull(result.getOrNull())
    }

    @Test
    fun `loading result reports flags correctly`() {
        val result = AppResult.Loading

        assertFalse(result.isSuccess)
        assertFalse(result.isError)
        assertTrue(result.isLoading)
        assertNull(result.getOrNull())
    }

    @Test
    fun `map transforms success value`() {
        val result = AppResult.Success(10)
        val mapped = result.map { it * 2 }

        assertTrue(mapped is AppResult.Success)
        assertEquals(20, (mapped as AppResult.Success).data)
    }

    @Test
    fun `map forwards error without transforming`() {
        val error = AppError.NotFound("Not found")
        val result: AppResult<Int> = AppResult.Error(error)
        val mapped = result.map { it * 2 }

        assertTrue(mapped is AppResult.Error)
        assertEquals(error, (mapped as AppResult.Error).error)
    }

    @Test
    fun `onSuccess and onError callbacks execute conditionally`() {
        var successCalled = false
        var errorCalled = false

        AppResult
            .Success("value")
            .onSuccess { successCalled = true }
            .onError { errorCalled = true }

        assertTrue(successCalled)
        assertFalse(errorCalled)

        successCalled = false
        errorCalled = false

        AppResult
            .Error(AppError.Validation(field = "email"))
            .onSuccess { successCalled = true }
            .onError { errorCalled = true }

        assertFalse(successCalled)
        assertTrue(errorCalled)
    }
}
