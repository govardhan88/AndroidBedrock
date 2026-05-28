package com.govi.androidbedrock.core.auth.di

import com.govi.androidbedrock.core.auth.AuthApiService
import com.govi.androidbedrock.core.auth.AuthInterceptor
import com.govi.androidbedrock.core.di.qualifier.AuthenticatedClient
import com.govi.androidbedrock.core.di.qualifier.AuthenticatedRetrofit
import com.govi.androidbedrock.core.di.qualifier.BasicClient
import com.govi.androidbedrock.core.di.qualifier.BasicRetrofit
import com.govi.androidbedrock.core.network.BaseUrlProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    @AuthenticatedClient
    fun provideAuthenticatedOkHttpClient(
        @BasicClient basicClient: OkHttpClient,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return basicClient.newBuilder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @AuthenticatedRetrofit
    fun provideAuthenticatedRetrofit(
        @AuthenticatedClient okHttpClient: OkHttpClient,
        json: Json,
        baseUrlProvider: BaseUrlProvider
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrlProvider.getBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@BasicRetrofit retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}
