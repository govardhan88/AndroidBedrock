package govi.feature.settings.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import govi.feature.settings.data.remote.SettingsApiService
import govi.feature.settings.data.repository.SettingsRepositoryImpl
import govi.feature.settings.domain.repository.SettingsRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsDataModule {
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideSettingsApiService(retrofit: Retrofit): SettingsApiService = retrofit.create(SettingsApiService::class.java)
    }
}
