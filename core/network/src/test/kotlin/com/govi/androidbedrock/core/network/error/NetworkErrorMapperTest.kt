package com.govi.androidbedrock.core.network.error

import com.govi.androidbedrock.core.base.result.AppError
import com.govi.androidbedrock.core.base.result.AppResult
import com.govi.androidbedrock.core.network.NetworkResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class NetworkErrorMapperTest {
    @Test
    fun mapHttpError_maps401ToAuthentication() {
        val error = NetworkErrorMapper.mapHttpError(401)
        assertTrue(error is AppError.Authentication)
        assertEquals("Session expired or authentication failed. Please sign in again.", error.message)
    }

    @Test
    fun mapHttpError_maps403ToAccessDenied() {
        val error = NetworkErrorMapper.mapHttpError(403)
        assertTrue(error is AppError.AccessDenied)
        assertEquals("You do not have permission to access this resource.", error.message)
    }

    @Test
    fun mapHttpError_maps404ToNotFound() {
        val error = NetworkErrorMapper.mapHttpError(404)
        assertTrue(error is AppError.NotFound)
        assertEquals("The requested resource was not found.", error.message)
    }

    @Test
    fun mapHttpError_maps408And504ToTimeout() {
        val timeout408 = NetworkErrorMapper.mapHttpError(408)
        assertTrue(timeout408 is AppError.Timeout)

        val timeout504 = NetworkErrorMapper.mapHttpError(504)
        assertTrue(timeout504 is AppError.Timeout)
    }

    @Test
    fun mapHttpError_maps409ToConflict() {
        val error = NetworkErrorMapper.mapHttpError(409)
        assertTrue(error is AppError.Conflict)
    }

    @Test
    fun mapHttpError_maps422ToValidation_withParsedFieldErrors() {
        val json =
            """
            {
                "message": "Invalid form fields",
                "errors": { "email": "Email is already taken" }
            }
            """.trimIndent()
        val error = NetworkErrorMapper.mapHttpError(422, errorBody = json)

        assertTrue(error is AppError.Validation)
        val validation = error as AppError.Validation
        assertEquals("Invalid form fields", validation.message)
        assertEquals("email", validation.field)
        assertEquals("Email is already taken", validation.errors["email"])
    }

    @Test
    fun mapHttpError_maps429ToRateLimitExceeded() {
        val error = NetworkErrorMapper.mapHttpError(429)
        assertTrue(error is AppError.RateLimitExceeded)
        assertEquals("Too many requests. Please wait a moment and try again.", error.message)
    }

    @Test
    fun mapHttpError_maps500And503ToServer() {
        val server500 = NetworkErrorMapper.mapHttpError(500)
        assertTrue(server500 is AppError.Server)
        assertEquals(500, (server500 as AppError.Server).code)

        val server503 = NetworkErrorMapper.mapHttpError(503)
        assertTrue(server503 is AppError.Server)
        assertEquals(503, (server503 as AppError.Server).code)
    }

    @Test
    fun mapHttpError_usesBackendMessage_whenProvidedInJson() {
        val json = """{"message": "Custom backend error description"}"""
        val error = NetworkErrorMapper.mapHttpError(400, errorBody = json)

        assertTrue(error is AppError.ClientError)
        assertEquals("Custom backend error description", error.message)
    }

    @Test
    fun mapThrowable_mapsExceptionsCorrectly() {
        val socketTimeout = NetworkErrorMapper.mapThrowable(SocketTimeoutException("Read timed out"))
        assertTrue(socketTimeout is AppError.Timeout)

        val unknownHost = NetworkErrorMapper.mapThrowable(UnknownHostException("No address found"))
        assertTrue(unknownHost is AppError.Network)

        val connectException = NetworkErrorMapper.mapThrowable(ConnectException("Connection refused"))
        assertTrue(connectException is AppError.Network)

        val sslException = NetworkErrorMapper.mapThrowable(SSLHandshakeException("Cert path untrusted"))
        assertTrue(sslException is AppError.Network)

        val ioException = NetworkErrorMapper.mapThrowable(IOException("Broken pipe"))
        assertTrue(ioException is AppError.Network)

        val unknown = NetworkErrorMapper.mapThrowable(IllegalStateException("Null state"))
        assertTrue(unknown is AppError.Unknown)
    }

    @Test
    fun toAppResult_convertsNetworkResultToAppResultProperly() {
        val successResult: NetworkResult<String> = NetworkResult.Success("test_data")
        val appSuccess = successResult.toAppResult()
        assertTrue(appSuccess is AppResult.Success)
        assertEquals("test_data", (appSuccess as AppResult.Success).data)

        val errorResult: NetworkResult<String> =
            NetworkResult.Error(code = 401, message = null, errorBody = null)
        val appError = errorResult.toAppResult()
        assertTrue(appError is AppResult.Error)
        assertTrue((appError as AppResult.Error).error is AppError.Authentication)

        val exceptionResult: NetworkResult<String> =
            NetworkResult.Exception(SocketTimeoutException("Timeout"))
        val appException = exceptionResult.toAppResult()
        assertTrue(appException is AppResult.Error)
        assertTrue((appException as AppResult.Error).error is AppError.Timeout)
    }
}
