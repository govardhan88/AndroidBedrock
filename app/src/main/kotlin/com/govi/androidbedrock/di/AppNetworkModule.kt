package com.govi.androidbedrock.di

import com.govi.androidbedrock.BuildConfig
import com.govi.androidbedrock.core.network.BaseUrlProvider
import com.govi.androidbedrock.core.network.NetworkLoggingConfig
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppBaseUrlProvider @Inject constructor() : BaseUrlProvider {
    override fun getBaseUrl(): String = BuildConfig.BASE_URL
}

@Singleton
class AppNetworkLoggingConfig @Inject constructor() : NetworkLoggingConfig {
    override val isDebug: Boolean = BuildConfig.DEBUG
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppNetworkModule {
    @Binds
    @Singleton
    abstract fun bindBaseUrlProvider(impl: AppBaseUrlProvider): BaseUrlProvider

    @Binds
    @Singleton
    abstract fun bindNetworkLoggingConfig(impl: AppNetworkLoggingConfig): NetworkLoggingConfig

    // To enable Bearer Token authentication & automatic 401 refresh in the app,
    // implement AuthTokenProvider (e.g. AppAuthTokenProvider) and uncomment:
    //
    // @Binds
    // @Singleton
    // abstract fun bindAuthTokenProvider(
    //     impl: AppAuthTokenProvider
    // ): com.govi.androidbedrock.core.network.auth.AuthTokenProvider
}
