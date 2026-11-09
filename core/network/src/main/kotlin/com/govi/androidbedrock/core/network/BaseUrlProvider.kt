package com.govi.androidbedrock.core.network

import javax.inject.Qualifier

/**
 * Qualifier for injecting or differentiating the network Base URL string.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

/**
 * Abstraction for resolving the API base URL.
 *
 * Hardcoding base URLs directly inside DI modules exposes internal endpoints,
 * prevents build-variant environment switching (Dev/Staging/Production), and blocks
 * runtime endpoint overrides (e.g. QA settings, dynamic failovers, mock servers in tests).
 */
fun interface BaseUrlProvider {
    fun getBaseUrl(): String
}
