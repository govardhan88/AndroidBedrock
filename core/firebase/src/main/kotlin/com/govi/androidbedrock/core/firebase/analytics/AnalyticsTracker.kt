package com.govi.androidbedrock.core.firebase.analytics

interface AnalyticsTracker {
    fun logEvent(
        name: String,
        params: Map<String, Any?> = emptyMap(),
    )

    fun setUserProperty(
        name: String,
        value: String?,
    )

    fun setUserId(userId: String?)

    fun resetAnalyticsData()
}

object AnalyticsEvents {
    const val SCREEN_VIEW = "screen_view"
    const val PARAM_SCREEN_NAME = "screen_name"
    const val PARAM_SCREEN_CLASS = "screen_class"
}

fun AnalyticsTracker.logScreenView(
    screenName: String,
    screenClass: String? = null,
) {
    logEvent(
        name = AnalyticsEvents.SCREEN_VIEW,
        params =
            buildMap {
                put(AnalyticsEvents.PARAM_SCREEN_NAME, screenName)
                if (screenClass != null) {
                    put(AnalyticsEvents.PARAM_SCREEN_CLASS, screenClass)
                }
            },
    )
}
