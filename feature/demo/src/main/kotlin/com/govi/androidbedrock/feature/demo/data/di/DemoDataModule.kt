package com.govi.androidbedrock.feature.demo.data.di

import com.govi.androidbedrock.feature.demo.data.repository.DemoRepositoryImpl
import com.govi.androidbedrock.feature.demo.domain.repository.DemoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoDataModule {
    @Binds
    @Singleton
    abstract fun bindDemoRepository(impl: DemoRepositoryImpl): DemoRepository
}
