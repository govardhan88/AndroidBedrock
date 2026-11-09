package com.govi.androidbedrock.core.network

import okhttp3.logging.HttpLoggingInterceptor

/**
 * Configuration contract for HTTP network logging.
 *
 * Implementations in application modules can dynamically toggle logging
 * based on [com.govi.androidbedrock.BuildConfig.DEBUG] or remote configuration flags.
 */
interface NetworkLoggingConfig {
    /**
     * Whether the application is running in debug/development mode.
     */
    val isDebug: Boolean

    /**
     * Logging detail level. Defaults to [HttpLoggingInterceptor.Level.BODY] in debug,
     * and [HttpLoggingInterceptor.Level.NONE] in production.
     */
    val level: HttpLoggingInterceptor.Level
        get() = if (isDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

    /**
     * Set of sensitive HTTP headers that must be redacted (masked with ██) in logs
     * to prevent credential and PII leakage.
     */
    val redactedHeaders: Set<String>
        get() =
            setOf(
                "Authorization",
                "Cookie",
                "Set-Cookie",
                "X-Api-Key",
                "Proxy-Authorization",
                "Token",
            )

    companion object {
        val Default =
            object : NetworkLoggingConfig {
                override val isDebug: Boolean = false
            }
    }
}
