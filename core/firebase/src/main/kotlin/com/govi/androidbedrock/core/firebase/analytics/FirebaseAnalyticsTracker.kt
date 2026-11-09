package com.govi.androidbedrock.core.firebase.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsTracker(
    private val analyticsProvider: () -> FirebaseAnalytics,
) : AnalyticsTracker {
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) : this({ FirebaseAnalytics.getInstance(context) })

    constructor(firebaseAnalytics: FirebaseAnalytics) : this({ firebaseAnalytics })

    override fun logEvent(
        name: String,
        params: Map<String, Any?>,
    ) {
        val bundle =
            if (params.isNotEmpty()) {
                Bundle().apply {
                    params.forEach { (key, value) ->
                        when (value) {
                            null -> Unit
                            is String -> putString(key, value)
                            is Int -> putInt(key, value)
                            is Long -> putLong(key, value)
                            is Double -> putDouble(key, value)
                            is Float -> putFloat(key, value)
                            is Boolean -> putBoolean(key, value)
                            else -> putString(key, value.toString())
                        }
                    }
                }
            } else {
                null
            }
        analyticsProvider().logEvent(name, bundle)
    }

    override fun setUserProperty(
        name: String,
        value: String?,
    ) {
        analyticsProvider().setUserProperty(name, value)
    }

    override fun setUserId(userId: String?) {
        analyticsProvider().setUserId(userId)
    }

    override fun resetAnalyticsData() {
        analyticsProvider().resetAnalyticsData()
    }
}
