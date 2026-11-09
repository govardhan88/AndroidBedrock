package com.govi.androidbedrock.core.network.di

import javax.inject.Qualifier

/**
 * Dagger qualifier for multibinding custom OkHttp [okhttp3.Interceptor] instances
 * into the main network client.
 *
 * Modules can contribute custom interceptors (e.g. Chucker, Cache, Analytics, Headers)
 * using `@Provides @IntoSet @NetworkInterceptors`.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NetworkInterceptors
