package com.govi.androidbedrock.core.base.di

import com.govi.androidbedrock.core.utils.provider.DefaultResourceProvider
import com.govi.androidbedrock.core.utils.provider.DefaultTimeProvider
import com.govi.androidbedrock.core.utils.provider.ResourceProvider
import com.govi.androidbedrock.core.utils.provider.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Base module for shared foundation dependencies.
 * Includes utilities that are cross-cutting across all features.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class BaseModule {

    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        defaultResourceProvider: DefaultResourceProvider
    ): ResourceProvider

    @Binds
    @Singleton
    abstract fun bindTimeProvider(
        defaultTimeProvider: DefaultTimeProvider
    ): TimeProvider
}
