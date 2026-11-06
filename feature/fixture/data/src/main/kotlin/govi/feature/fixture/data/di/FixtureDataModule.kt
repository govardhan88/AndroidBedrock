package govi.feature.fixture.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import govi.feature.fixture.data.remote.FixtureApiService
import govi.feature.fixture.data.repository.FixtureRepositoryImpl
import govi.feature.fixture.domain.repository.FixtureRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FixtureDataModule {

    @Binds
    @Singleton
    abstract fun bindFixtureRepository(
        impl: FixtureRepositoryImpl
    ): FixtureRepository

    companion object {
        @Provides
        @Singleton
        fun provideFixtureApiService(retrofit: Retrofit): FixtureApiService {
            return retrofit.create(FixtureApiService::class.java)
        }
    }
}
