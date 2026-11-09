package com.govi.androidbedrock.core.firebase

import com.govi.androidbedrock.core.firebase.analytics.AnalyticsEvents
import com.govi.androidbedrock.core.firebase.analytics.AnalyticsTracker
import com.govi.androidbedrock.core.firebase.analytics.NoOpAnalyticsTracker
import com.govi.androidbedrock.core.firebase.analytics.logScreenView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AnalyticsTrackerTest {
    private class TestAnalyticsTracker : AnalyticsTracker {
        var lastEventName: String? = null
        var lastParams: Map<String, Any?> = emptyMap()
        var lastUserProperty: Pair<String, String?>? = null
        var lastUserId: String? = null
        var resetCount: Int = 0

        override fun logEvent(
            name: String,
            params: Map<String, Any?>,
        ) {
            lastEventName = name
            lastParams = params
        }

        override fun setUserProperty(
            name: String,
            value: String?,
        ) {
            lastUserProperty = name to value
        }

        override fun setUserId(userId: String?) {
            lastUserId = userId
        }

        override fun resetAnalyticsData() {
            resetCount++
        }
    }

    @Test
    fun logEvent_capturesEventAndParameters() {
        val tracker = TestAnalyticsTracker()
        tracker.logEvent("purchase", mapOf("item_id" to "123", "amount" to 99.99))

        assertEquals("purchase", tracker.lastEventName)
        assertEquals("123", tracker.lastParams["item_id"])
        assertEquals(99.99, tracker.lastParams["amount"])
    }

    @Test
    fun logScreenView_logsCorrectScreenEvent() {
        val tracker = TestAnalyticsTracker()
        tracker.logScreenView(screenName = "Home", screenClass = "HomeScreen")

        assertEquals(AnalyticsEvents.SCREEN_VIEW, tracker.lastEventName)
        assertEquals("Home", tracker.lastParams[AnalyticsEvents.PARAM_SCREEN_NAME])
        assertEquals("HomeScreen", tracker.lastParams[AnalyticsEvents.PARAM_SCREEN_CLASS])
    }

    @Test
    fun logScreenView_withoutClass_omitsScreenClass() {
        val tracker = TestAnalyticsTracker()
        tracker.logScreenView(screenName = "Settings")

        assertEquals(AnalyticsEvents.SCREEN_VIEW, tracker.lastEventName)
        assertEquals("Settings", tracker.lastParams[AnalyticsEvents.PARAM_SCREEN_NAME])
        assertNull(tracker.lastParams[AnalyticsEvents.PARAM_SCREEN_CLASS])
    }

    @Test
    fun setUserProperty_and_setUserId() {
        val tracker = TestAnalyticsTracker()
        tracker.setUserProperty("premium", "true")
        tracker.setUserId("user_42")

        assertEquals("premium" to "true", tracker.lastUserProperty)
        assertEquals("user_42", tracker.lastUserId)
    }

    @Test
    fun resetAnalyticsData_incrementsCounter() {
        val tracker = TestAnalyticsTracker()
        tracker.resetAnalyticsData()

        assertEquals(1, tracker.resetCount)
    }

    @Test
    fun noOpAnalyticsTracker_doesNotThrow() {
        val noOp = NoOpAnalyticsTracker()
        noOp.logEvent("test")
        noOp.logScreenView("test_screen")
        noOp.setUserProperty("key", "val")
        noOp.setUserId("id")
        noOp.resetAnalyticsData()
    }
}
