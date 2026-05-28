package com.govi.androidbedrock.demo.di

import com.govi.androidbedrock.core.di.qualifier.AuthenticatedRetrofit
import com.govi.androidbedrock.demo.data.DemoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DemoModule {

    @Provides
    @Singleton
    fun provideDemoApiService(@AuthenticatedRetrofit retrofit: Retrofit): DemoApiService {
        return retrofit.create(DemoApiService::class.java)
    }
}
