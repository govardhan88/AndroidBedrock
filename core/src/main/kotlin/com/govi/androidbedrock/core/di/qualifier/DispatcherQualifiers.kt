package com.govi.androidbedrock.core.di.qualifier

import javax.inject.Qualifier

/**
 * Qualifier for the Main coroutine dispatcher.
 * Used for UI-related tasks.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

/**
 * Qualifier for the IO coroutine dispatcher.
 * Used for disk and network IO.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * Qualifier for the Default coroutine dispatcher.
 * Used for CPU-intensive tasks.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

/**
 * Qualifier for the Unconfined coroutine dispatcher.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnconfinedDispatcher
