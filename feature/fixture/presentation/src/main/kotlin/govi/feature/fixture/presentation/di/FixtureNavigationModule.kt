package govi.feature.fixture.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import govi.core.navigation.FeatureEntry
import govi.feature.fixture.presentation.navigation.FixtureFeatureEntryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class FixtureNavigationModule {

    @Binds
    @IntoSet
    abstract fun bindFixtureFeatureEntry(
        entry: FixtureFeatureEntryImpl
    ): FeatureEntry
}
