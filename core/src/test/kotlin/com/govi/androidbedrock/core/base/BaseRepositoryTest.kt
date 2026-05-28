package com.govi.androidbedrock.core.base

import org.junit.Assert.*
import org.junit.Test

class BaseRepositoryTest {

    private val repository = object : BaseRepository() {}

    @Test
    fun testSafeApiCall_Success() {
        val result = runBlockingTest {
            repository.safeApiCall {
                "success"
            }
        }
        assertTrue(result.isSuccess())
        assertEquals("success", result.getOrNull())
    }

    @Test
    fun testSafeApiCall_Exception() {
        val result = runBlockingTest {
            repository.safeApiCall {
                throw Exception("test error")
            }
        }
        assertTrue(result.isError())
        assertNotNull(result.exceptionOrNull())
    }

    private fun <T> runBlockingTest(block: suspend () -> T): T {
        return kotlinx.coroutines.runBlocking { block() }
    }
}
