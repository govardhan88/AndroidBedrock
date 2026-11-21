package com.govi.androidbedrock.feature.demo.presentation.di

import com.govi.androidbedrock.core.navigation.FeatureEntry
import com.govi.androidbedrock.feature.demo.presentation.navigation.DemoFeatureEntryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindDemoFeatureEntry(entry: DemoFeatureEntryImpl): FeatureEntry
}
