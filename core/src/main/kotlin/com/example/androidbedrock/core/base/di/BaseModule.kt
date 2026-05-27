package com.example.androidbedrock.core.base.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Base module for shared dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object BaseModule {
    // Add common base dependencies here
}
