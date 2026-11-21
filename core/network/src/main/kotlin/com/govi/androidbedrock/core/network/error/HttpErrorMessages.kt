package com.govi.androidbedrock.core.network.error

/**
 * Standard user-facing message mapping for HTTP status codes.
 * Ensures consistent, human-readable fallback messages even when HTTP/2
 * reason phrases are omitted or server response bodies are missing.
 */
object HttpErrorMessages {
    private val codeMessages: Map<Int, String> =
        mapOf(
            // 4xx Client Errors
            400 to "The request could not be processed due to invalid parameters.",
            401 to "Session expired or authentication failed. Please sign in again.",
            403 to "You do not have permission to access this resource.",
            404 to "The requested resource was not found.",
            405 to "The requested action is not supported for this resource.",
            406 to "The requested format is not acceptable.",
            408 to "The request timed out. Please check your connection and try again.",
            409 to "A conflict occurred with the current state of the resource.",
            410 to "The requested resource is no longer available.",
            413 to "The request payload exceeds the allowed size limit.",
            415 to "The request format or media type is unsupported.",
            422 to "Unable to process the request. Please check your input and try again.",
            429 to "Too many requests. Please wait a moment and try again.",
            // 5xx Server Errors
            500 to "The server encountered an error. Please try again later.",
            501 to "The requested service operation is not supported by the server.",
            502 to "Bad gateway. Received an invalid response from the upstream server.",
            503 to "Service is temporarily unavailable. Maintenance may be in progress.",
            504 to "Gateway timed out waiting for the server response.",
            505 to "The server does not support the HTTP protocol version used.",
        )

    /**
     * Resolves a user-friendly error message for the given HTTP status code.
     */
    fun getMessage(code: Int): String =
        codeMessages[code]
            ?: when (code) {
                in 400..499 -> "Client error occurred (HTTP $code)."
                in 500..599 -> "Server error occurred (HTTP $code). Please try again later."
                else -> "An unexpected error occurred (HTTP $code)."
            }
}
