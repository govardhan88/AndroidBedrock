package com.govi.androidbedrock.core.firebase.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseCrashReporter(
    private val crashlyticsProvider: () -> FirebaseCrashlytics,
) : CrashReporter {
    @Inject
    constructor() : this({ FirebaseCrashlytics.getInstance() })

    constructor(crashlytics: FirebaseCrashlytics) : this({ crashlytics })

    override fun recordException(throwable: Throwable) {
        crashlyticsProvider().recordException(throwable)
    }

    override fun log(message: String) {
        crashlyticsProvider().log(message)
    }

    override fun setUserId(userId: String) {
        crashlyticsProvider().setUserId(userId)
    }

    override fun setCustomKey(
        key: String,
        value: String,
    ) {
        crashlyticsProvider().setCustomKey(key, value)
    }

    override fun setCustomKey(
        key: String,
        value: Boolean,
    ) {
        crashlyticsProvider().setCustomKey(key, value)
    }

    override fun setCustomKey(
        key: String,
        value: Int,
    ) {
        crashlyticsProvider().setCustomKey(key, value)
    }

    override fun setCustomKey(
        key: String,
        value: Long,
    ) {
        crashlyticsProvider().setCustomKey(key, value)
    }

    override fun setCustomKey(
        key: String,
        value: Double,
    ) {
        crashlyticsProvider().setCustomKey(key, value)
    }

    override fun setCustomKey(
        key: String,
        value: Float,
    ) {
        crashlyticsProvider().setCustomKey(key, value)
    }

    override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        crashlyticsProvider().setCrashlyticsCollectionEnabled(enabled)
    }
}
