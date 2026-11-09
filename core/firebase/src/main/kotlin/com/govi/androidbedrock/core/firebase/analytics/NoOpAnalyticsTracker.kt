package com.govi.androidbedrock.core.firebase.analytics

class NoOpAnalyticsTracker : AnalyticsTracker {
    override fun logEvent(
        name: String,
        params: Map<String, Any?>,
    ) = Unit

    override fun setUserProperty(
        name: String,
        value: String?,
    ) = Unit

    override fun setUserId(userId: String?) = Unit

    override fun resetAnalyticsData() = Unit
}
