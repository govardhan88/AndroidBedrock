package govi.core.navigation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import govi.core.navigation.AppNavigator
import govi.core.navigation.AppNavigatorImpl
import govi.core.navigation.FeatureEntry
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
