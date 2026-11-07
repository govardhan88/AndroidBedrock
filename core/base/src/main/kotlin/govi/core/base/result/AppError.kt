package govi.core.base.result

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

    data class Server(
        val code: Int? = null,
        override val message: String = "Server error occurred. Please try again later.",
        override val cause: Throwable? = null,
    ) : AppError

    data class Authentication(
        override val message: String = "Session expired or authentication failed.",
        override val cause: Throwable? = null,
    ) : AppError

    data class NotFound(
        override val message: String = "Requested resource was not found.",
        override val cause: Throwable? = null,
    ) : AppError

    data class Validation(
        val field: String? = null,
        override val message: String = "Validation failed.",
        override val cause: Throwable? = null,
    ) : AppError

    data class Unknown(
        override val message: String = "An unexpected error occurred.",
        override val cause: Throwable? = null,
    ) : AppError
}
