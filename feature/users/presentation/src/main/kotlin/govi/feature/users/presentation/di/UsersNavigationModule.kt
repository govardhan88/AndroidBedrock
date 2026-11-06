package govi.feature.users.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import govi.core.navigation.FeatureEntry
import govi.feature.users.presentation.navigation.UsersFeatureEntryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class UsersNavigationModule {

    @Binds
    @IntoSet
    abstract fun bindUsersFeatureEntry(
        entry: UsersFeatureEntryImpl
    ): FeatureEntry
}
