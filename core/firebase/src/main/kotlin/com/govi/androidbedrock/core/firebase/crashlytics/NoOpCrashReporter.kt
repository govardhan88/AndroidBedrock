package com.govi.androidbedrock.core.firebase.crashlytics

class NoOpCrashReporter : CrashReporter {
    override fun recordException(throwable: Throwable) = Unit

    override fun log(message: String) = Unit

    override fun setUserId(userId: String) = Unit

    override fun setCustomKey(
        key: String,
        value: String,
    ) = Unit

    override fun setCustomKey(
        key: String,
        value: Boolean,
    ) = Unit

    override fun setCustomKey(
        key: String,
        value: Int,
    ) = Unit

    override fun setCustomKey(
        key: String,
        value: Long,
    ) = Unit

    override fun setCustomKey(
        key: String,
        value: Double,
    ) = Unit

    override fun setCustomKey(
        key: String,
        value: Float,
    ) = Unit

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) = Unit
}
