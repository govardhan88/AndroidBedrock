package govi.core.base.dispatcher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppDispatchersTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `default dispatchers provide standard coroutine dispatchers`() {
        val dispatchers = DefaultAppDispatchers()

        assertEquals(Dispatchers.IO, dispatchers.io)
        assertEquals(Dispatchers.Default, dispatchers.default)
        assertEquals(Dispatchers.Unconfined, dispatchers.unconfined)
        assertEquals(Dispatchers.Main, dispatchers.main)
    }
}
