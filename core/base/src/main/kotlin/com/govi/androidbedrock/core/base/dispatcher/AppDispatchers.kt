package com.govi.androidbedrock.core.base.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Centralized interface providing thread dispatchers throughout the application.
 *
 * Decouples code from static [kotlinx.coroutines.Dispatchers] to allow 100% deterministic unit testing.
 */
interface AppDispatchers {
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val main: CoroutineDispatcher
    val mainImmediate: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}
