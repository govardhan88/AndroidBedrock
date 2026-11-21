package com.govi.androidbedrock.core.network.error

import com.govi.androidbedrock.core.base.result.AppError
import com.govi.androidbedrock.core.base.result.AppResult
import com.govi.androidbedrock.core.network.NetworkResult
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Maps [NetworkResult], HTTP status codes, and network exceptions into strongly-typed [AppError]s
 * and [AppResult]s.
 */
object NetworkErrorMapper {
    /**
     * Maps an HTTP response failure into the corresponding domain [AppError].
     *
     * @param code HTTP status code (e.g. 401, 404, 500).
     * @param rawMessage HTTP status message or reason phrase (may be blank in HTTP/2).
     * @param errorBody Optional raw response body (usually JSON).
     */
    fun mapHttpError(
        code: Int,
        rawMessage: String? = null,
        errorBody: String? = null,
    ): AppError {
        val parsed = RemoteErrorParser.parse(errorBody)
        val defaultMessage = HttpErrorMessages.getMessage(code)
        val resolvedMessage =
            parsed?.message
                ?: rawMessage?.takeIf { it.isNotBlank() }
                ?: defaultMessage

        return when (code) {
            401 -> AppError.Authentication(message = resolvedMessage)
            403 -> AppError.AccessDenied(message = resolvedMessage)
            404 -> AppError.NotFound(message = resolvedMessage)
            408, 504 -> AppError.Timeout(message = resolvedMessage)
            409 -> AppError.Conflict(message = resolvedMessage)
            422 -> {
                val primaryField = parsed?.fieldErrors?.keys?.firstOrNull()
                AppError.Validation(
                    field = primaryField,
                    errors = parsed?.fieldErrors ?: emptyMap(),
                    message = resolvedMessage,
                )
            }
            429 -> AppError.RateLimitExceeded(message = resolvedMessage)
            in 400..499 -> AppError.ClientError(code = code, message = resolvedMessage)
            in 500..599 -> AppError.Server(code = code, message = resolvedMessage)
            else -> AppError.Unknown(message = resolvedMessage)
        }
    }

    /**
     * Maps network and IO throwables into user-friendly domain [AppError]s.
     */
    fun mapThrowable(throwable: Throwable): AppError =
        when (throwable) {
            is SocketTimeoutException -> {
                AppError.Timeout(
                    message = "Connection timed out. Please check your internet connection and try again.",
                    cause = throwable,
                )
            }
            is UnknownHostException -> {
                AppError.Network(
                    message = "Unable to reach server. Please check your internet connection.",
                    cause = throwable,
                )
            }
            is ConnectException -> {
                AppError.Network(
                    message = "Failed to connect to the server. Please try again later.",
                    cause = throwable,
                )
            }
            is SSLException -> {
                AppError.Network(
                    message = "A secure connection could not be established.",
                    cause = throwable,
                )
            }
            is IOException -> {
                AppError.Network(
                    message = "A network error occurred. Please check your connection and try again.",
                    cause = throwable,
                )
            }
            else -> {
                AppError.Unknown(
                    message =
                        throwable.localizedMessage?.takeIf { it.isNotBlank() }
                            ?: "An unexpected error occurred.",
                    cause = throwable,
                )
            }
        }
}

/**
 * Extension bridging [NetworkResult] to domain [AppResult].
 */
fun <T> NetworkResult<T>.toAppResult(): AppResult<T> =
    when (this) {
        is NetworkResult.Success -> AppResult.Success(data)
        is NetworkResult.Error -> AppResult.Error(toAppError())
        is NetworkResult.Exception -> AppResult.Error(throwable.toAppError())
    }

/**
 * Converts a [NetworkResult.Error] into an [AppError].
 */
fun NetworkResult.Error.toAppError(): AppError = NetworkErrorMapper.mapHttpError(code = code, rawMessage = message, errorBody = errorBody)

/**
 * Converts a [Throwable] into an [AppError].
 */
fun Throwable.toAppError(): AppError = NetworkErrorMapper.mapThrowable(this)
