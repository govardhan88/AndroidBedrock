package com.govi.androidbedrock.core.network

import java.io.IOException

/**
 * Base exception for API errors
 */
sealed class ApiException : IOException() {
    data class HttpException(
        val code: Int,
        override val message: String,
        val body: String? = null
    ) : ApiException()

    data class NetworkException(
        override val message: String,
        override val cause: Throwable? = null
    ) : ApiException()

    data class ParseException(
        override val message: String,
        override val cause: Throwable? = null
    ) : ApiException()

    data class UnknownException(
        override val message: String = "Unknown error occurred",
        override val cause: Throwable? = null
    ) : ApiException()
}
