package com.govi.androidbedrock.core.firebase.crashlytics

interface CrashReporter {
    fun recordException(throwable: Throwable)

    fun log(message: String)

    fun setUserId(userId: String)

    fun setCustomKey(
        key: String,
        value: String,
    )

    fun setCustomKey(
        key: String,
        value: Boolean,
    )

    fun setCustomKey(
        key: String,
        value: Int,
    )

    fun setCustomKey(
        key: String,
        value: Long,
    )

    fun setCustomKey(
        key: String,
        value: Double,
    )

    fun setCustomKey(
        key: String,
        value: Float,
    )

    fun setCrashlyticsCollectionEnabled(enabled: Boolean)
}
