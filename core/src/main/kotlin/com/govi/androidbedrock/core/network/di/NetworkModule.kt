package com.govi.androidbedrock.core.network.di

import com.govi.androidbedrock.core.di.qualifier.BasicClient
import com.govi.androidbedrock.core.di.qualifier.BasicRetrofit
import com.govi.androidbedrock.core.network.BaseUrlProvider
import com.govi.androidbedrock.core.network.DefaultBaseUrlProvider
import com.govi.androidbedrock.core.network.interceptor.DynamicBaseUrlInterceptor
import com.govi.androidbedrock.core.network.interceptor.ErrorHandlingInterceptor
import com.govi.androidbedrock.core.utils.NetworkConstants
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindBaseUrlProvider(
        defaultBaseUrlProvider: DefaultBaseUrlProvider
    ): BaseUrlProvider

    companion object {
        @Provides
        @Singleton
        @BasicClient
        fun provideBasicOkHttpClient(
            dynamicBaseUrlInterceptor: DynamicBaseUrlInterceptor,
            errorHandlingInterceptor: ErrorHandlingInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(dynamicBaseUrlInterceptor)
                .addInterceptor(errorHandlingInterceptor)
                .connectTimeout(NetworkConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(NetworkConstants.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(NetworkConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }

        @Provides
        @Singleton
        @BasicRetrofit
        fun provideBasicRetrofit(
            @BasicClient okHttpClient: OkHttpClient,
            json: Json,
            baseUrlProvider: BaseUrlProvider
        ): Retrofit {
            val contentType = NetworkConstants.CONTENT_TYPE_JSON.toMediaType()
            return Retrofit.Builder()
                .baseUrl(baseUrlProvider.getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
        }
    }
}
