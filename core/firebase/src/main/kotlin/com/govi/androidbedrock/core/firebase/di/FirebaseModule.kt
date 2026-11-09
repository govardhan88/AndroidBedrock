package com.govi.androidbedrock.core.firebase.di

import com.govi.androidbedrock.core.firebase.analytics.AnalyticsTracker
import com.govi.androidbedrock.core.firebase.analytics.FirebaseAnalyticsTracker
import com.govi.androidbedrock.core.firebase.crashlytics.CrashReporter
import com.govi.androidbedrock.core.firebase.crashlytics.FirebaseCrashReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FirebaseModule {
    @Binds
    @Singleton
    fun bindAnalyticsTracker(impl: FirebaseAnalyticsTracker): AnalyticsTracker

    @Binds
    @Singleton
    fun bindCrashReporter(impl: FirebaseCrashReporter): CrashReporter
}
