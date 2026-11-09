package com.govi.androidbedrock.core.navigation.di

import com.govi.androidbedrock.core.navigation.AppNavigator
import com.govi.androidbedrock.core.navigation.AppNavigatorImpl
import com.govi.androidbedrock.core.navigation.FeatureEntry
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {
    @Binds
    @Singleton
    abstract fun bindAppNavigator(impl: AppNavigatorImpl): AppNavigator

    @Multibinds
    abstract fun bindFeatureEntries(): Set<FeatureEntry>
}
