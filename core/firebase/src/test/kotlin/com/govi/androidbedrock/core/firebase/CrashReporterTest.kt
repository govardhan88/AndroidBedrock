package com.govi.androidbedrock.core.firebase

import com.govi.androidbedrock.core.firebase.crashlytics.CrashReporter
import com.govi.androidbedrock.core.firebase.crashlytics.NoOpCrashReporter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrashReporterTest {
    private class TestCrashReporter : CrashReporter {
        var lastException: Throwable? = null
        var lastLogMessage: String? = null
        var lastUserId: String? = null
        val customKeys = mutableMapOf<String, Any>()
        var collectionEnabled: Boolean? = null

        override fun recordException(throwable: Throwable) {
            lastException = throwable
        }

        override fun log(message: String) {
            lastLogMessage = message
        }

        override fun setUserId(userId: String) {
            lastUserId = userId
        }

        override fun setCustomKey(
            key: String,
            value: String,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Boolean,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Int,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Long,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Double,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Float,
        ) {
            customKeys[key] = value
        }

        override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
            collectionEnabled = enabled
        }
    }

    @Test
    fun recordException_storesThrowable() {
        val reporter = TestCrashReporter()
        val exception = IllegalStateException("Test failure")
        reporter.recordException(exception)

        assertEquals(exception, reporter.lastException)
    }

    @Test
    fun log_storesMessage() {
        val reporter = TestCrashReporter()
        reporter.log("Application started")

        assertEquals("Application started", reporter.lastLogMessage)
    }

    @Test
    fun setUserId_storesId() {
        val reporter = TestCrashReporter()
        reporter.setUserId("user_100")

        assertEquals("user_100", reporter.lastUserId)
    }

    @Test
    fun setCustomKeys_storesDiverseTypes() {
        val reporter = TestCrashReporter()
        reporter.setCustomKey("string_key", "value")
        reporter.setCustomKey("bool_key", true)
        reporter.setCustomKey("int_key", 42)
        reporter.setCustomKey("long_key", 1000L)
        reporter.setCustomKey("double_key", 3.14)
        reporter.setCustomKey("float_key", 1.5f)

        assertEquals("value", reporter.customKeys["string_key"])
        assertEquals(true, reporter.customKeys["bool_key"])
        assertEquals(42, reporter.customKeys["int_key"])
        assertEquals(1000L, reporter.customKeys["long_key"])
        assertEquals(3.14, reporter.customKeys["double_key"])
        assertEquals(1.5f, reporter.customKeys["float_key"])
    }

    @Test
    fun setCrashlyticsCollectionEnabled_updatesState() {
        val reporter = TestCrashReporter()
        reporter.setCrashlyticsCollectionEnabled(true)

        assertTrue(reporter.collectionEnabled == true)
    }

    @Test
    fun noOpCrashReporter_doesNotThrow() {
        val noOp = NoOpCrashReporter()
        noOp.recordException(RuntimeException("No-op test"))
        noOp.log("No-op log")
        noOp.setUserId("id")
        noOp.setCustomKey("key", "val")
        noOp.setCustomKey("key", true)
        noOp.setCustomKey("key", 1)
        noOp.setCustomKey("key", 1L)
        noOp.setCustomKey("key", 1.0)
        noOp.setCustomKey("key", 1.0f)
        noOp.setCrashlyticsCollectionEnabled(false)
    }
}
