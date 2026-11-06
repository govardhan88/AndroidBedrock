package govi.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import govi.app.BuildConfig
import govi.core.network.BaseUrlProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppBaseUrlProvider @Inject constructor() : BaseUrlProvider {
    override fun getBaseUrl(): String = BuildConfig.BASE_URL
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AppNetworkModule {

    @Binds
    @Singleton
    abstract fun bindBaseUrlProvider(
        impl: AppBaseUrlProvider
    ): BaseUrlProvider
}
