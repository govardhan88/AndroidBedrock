package govi.feature.users.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import govi.feature.users.data.remote.UserApiService
import govi.feature.users.data.repository.UserRepositoryImpl
import govi.feature.users.domain.repository.UserRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UsersDataModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    companion object {
        @Provides
        @Singleton
        fun provideUserApiService(retrofit: Retrofit): UserApiService = retrofit.create(UserApiService::class.java)
    }
}
