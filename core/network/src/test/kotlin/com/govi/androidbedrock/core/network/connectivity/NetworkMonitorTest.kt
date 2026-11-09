package com.govi.androidbedrock.core.network.connectivity

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkMonitorTest {
    class FakeNetworkMonitor(
        initialOnline: Boolean = true,
    ) : NetworkMonitor {
        private val _isOnline = MutableStateFlow(initialOnline)
        override val isOnline = _isOnline.asStateFlow()
        override val isCurrentlyOnline: Boolean get() = _isOnline.value

        fun setOnline(online: Boolean) {
            _isOnline.value = online
        }
    }

    @Test
    fun `initial state emits true when created with default online status`() =
        runTest {
            val monitor = FakeNetworkMonitor(initialOnline = true)
            assertTrue(monitor.isCurrentlyOnline)
            assertTrue(monitor.isOnline.first())
        }

    @Test
    fun `initial state emits false when created with offline status`() =
        runTest {
            val monitor = FakeNetworkMonitor(initialOnline = false)
            assertFalse(monitor.isCurrentlyOnline)
            assertFalse(monitor.isOnline.first())
        }

    @Test
    fun `updating network status emits new values to collectors`() =
        runTest {
            val monitor = FakeNetworkMonitor(initialOnline = true)
            assertEquals(true, monitor.isOnline.first())

            monitor.setOnline(false)
            assertEquals(false, monitor.isCurrentlyOnline)
            assertEquals(false, monitor.isOnline.first())

            monitor.setOnline(true)
            assertEquals(true, monitor.isCurrentlyOnline)
            assertEquals(true, monitor.isOnline.first())
        }
}
