package com.govi.androidbedrock.core.di

import com.govi.androidbedrock.core.base.di.BaseModule
import com.govi.androidbedrock.core.network.di.NetworkModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Parent module that aggregates all core-related modules.
 */
@Module(
    includes = [
        BaseModule::class,
        DispatcherModule::class,
        StorageModule::class,
        JsonModule::class,
        NetworkModule::class
    ]
)
@InstallIn(SingletonComponent::class)
object CoreModule
