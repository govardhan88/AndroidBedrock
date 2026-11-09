package com.govi.androidbedrock.core.network.di

import com.govi.androidbedrock.core.network.BaseUrlProvider
import com.govi.androidbedrock.core.network.NetworkLoggingConfig
import com.govi.androidbedrock.core.network.auth.AuthInterceptor
import com.govi.androidbedrock.core.network.auth.AuthTokenProvider
import com.govi.androidbedrock.core.network.auth.TokenAuthenticator
import dagger.BindsOptionalOf
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.Optional
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NetworkDeclarationModule {
    @BindsOptionalOf
    fun bindAuthTokenProvider(): AuthTokenProvider

    @BindsOptionalOf
    fun bindNetworkLoggingConfig(): NetworkLoggingConfig

    @Multibinds
    @NetworkInterceptors
    fun bindCustomInterceptors(): Set<Interceptor>
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            encodeDefaults = true
            prettyPrint = false
        }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(loggingConfig: Optional<NetworkLoggingConfig>): HttpLoggingInterceptor {
        val config = loggingConfig.orElse(NetworkLoggingConfig.Default)

        // Custom chunking logger to avoid Android Logcat 4KB truncation
        val chunkingLogger =
            HttpLoggingInterceptor.Logger { message ->
                val maxLogSize = 3500
                var i = 0
                while (i < message.length) {
                    val end = minOf(message.length, i + maxLogSize)
                    android.util.Log.d("OkHttp", message.substring(i, end))
                    i = end
                }
            }

        return HttpLoggingInterceptor(chunkingLogger).apply {
            level = config.level
            config.redactedHeaders.forEach { redactHeader(it) }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        loggingConfig: Optional<NetworkLoggingConfig>,
        authTokenProvider: Optional<AuthTokenProvider>,
        @NetworkInterceptors customInterceptors: Set<@JvmSuppressWildcards Interceptor>,
    ): OkHttpClient {
        val config = loggingConfig.orElse(NetworkLoggingConfig.Default)

        return OkHttpClient
            .Builder()
            .apply {
                // 1. Pluggable Auth Interceptor & Authenticator (zero overhead when unbound)
                if (authTokenProvider.isPresent) {
                    val provider = authTokenProvider.get()
                    addInterceptor(AuthInterceptor(provider))
                    authenticator(TokenAuthenticator(provider))
                }

                // 2. Multibound custom interceptors (Chucker, Cache, Analytics, etc.)
                customInterceptors.forEach { interceptor ->
                    addInterceptor(interceptor)
                }

                // 3. Optimized logging interceptor (only registered in debug builds)
                if (config.isDebug) {
                    addInterceptor(loggingInterceptor)
                }

                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
            }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
        baseUrlProvider: BaseUrlProvider,
    ): Retrofit {
        val rawUrl = baseUrlProvider.getBaseUrl()
        val formattedUrl = if (rawUrl.endsWith("/")) rawUrl else "$rawUrl/"

        require(
            formattedUrl.startsWith("https://") ||
                formattedUrl.startsWith("http://localhost") ||
                formattedUrl.startsWith("http://10.0.2.2"),
        ) {
            "Security violation: Production Base URL must use HTTPS. Cleartext HTTP is prohibited: $formattedUrl"
        }

        val contentType = "application/json".toMediaType()

        return Retrofit
            .Builder()
            .baseUrl(formattedUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}
