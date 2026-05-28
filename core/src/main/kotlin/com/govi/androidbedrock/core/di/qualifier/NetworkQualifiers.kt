package com.govi.androidbedrock.core.di.qualifier

import javax.inject.Qualifier

/**
 * Qualifier for an authenticated OkHttpClient.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedClient

/**
 * Qualifier for a basic (unauthenticated) OkHttpClient.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BasicClient

/**
 * Qualifier for a Retrofit instance using an authenticated client.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedRetrofit

/**
 * Qualifier for a Retrofit instance using a basic client.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BasicRetrofit
