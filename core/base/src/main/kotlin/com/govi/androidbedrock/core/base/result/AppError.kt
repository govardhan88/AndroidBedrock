package com.govi.androidbedrock.core.base.result

/**
 * Strongly-typed domain and infrastructure error hierarchy.
 */
sealed interface AppError {
    val message: String
    val cause: Throwable?

    data class Network(
        override val message: String = "No internet connection available.",
        override val cause: Throwable? = null,
    ) : AppError

    data class Timeout(
        override val message: String = "Connection timed out. Please try again.",
        override val cause: Throwable? = null,
    ) : AppError

    data class Server(
        val code: Int? = null,
        override val message: String = "Server error occurred. Please try again later.",
        override val cause: Throwable? = null,
    ) : AppError

    data class Authentication(
        override val message: String = "Session expired or authentication failed.",
        override val cause: Throwable? = null,
    ) : AppError

    data class AccessDenied(
        override val message: String = "You do not have permission to access this resource.",
        override val cause: Throwable? = null,
    ) : AppError

    data class NotFound(
        override val message: String = "Requested resource was not found.",
        override val cause: Throwable? = null,
    ) : AppError

    data class Conflict(
        override val message: String = "A conflict occurred with the current state of the resource.",
        override val cause: Throwable? = null,
    ) : AppError

    data class RateLimitExceeded(
        val retryAfterSeconds: Long? = null,
        override val message: String = "Too many requests. Please wait a moment and try again.",
        override val cause: Throwable? = null,
    ) : AppError

    data class Validation(
        val field: String? = null,
        val errors: Map<String, String> = emptyMap(),
        override val message: String = "Validation failed.",
        override val cause: Throwable? = null,
    ) : AppError

    data class ClientError(
        val code: Int? = null,
        override val message: String = "The request could not be processed.",
        override val cause: Throwable? = null,
    ) : AppError

    data class Unknown(
        override val message: String = "An unexpected error occurred.",
        override val cause: Throwable? = null,
    ) : AppError
}
