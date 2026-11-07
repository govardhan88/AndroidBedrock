package govi.feature.settings.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import govi.core.navigation.FeatureEntry
import govi.feature.settings.presentation.navigation.SettingsFeatureEntryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsNavigationModule {
    @Binds
    @IntoSet
    abstract fun bindSettingsFeatureEntry(entry: SettingsFeatureEntryImpl): FeatureEntry
}
