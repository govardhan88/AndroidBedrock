package com.govi.androidbedrock.core.network.error

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HttpErrorMessagesTest {
    @Test
    fun getMessage_returnsExactMessage_forStandardClientErrors() {
        assertEquals(
            "The request could not be processed due to invalid parameters.",
            HttpErrorMessages.getMessage(400),
        )
        assertEquals(
            "Session expired or authentication failed. Please sign in again.",
            HttpErrorMessages.getMessage(401),
        )
        assertEquals(
            "You do not have permission to access this resource.",
            HttpErrorMessages.getMessage(403),
        )
        assertEquals(
            "The requested resource was not found.",
            HttpErrorMessages.getMessage(404),
        )
        assertEquals(
            "The request timed out. Please check your connection and try again.",
            HttpErrorMessages.getMessage(408),
        )
        assertEquals(
            "A conflict occurred with the current state of the resource.",
            HttpErrorMessages.getMessage(409),
        )
        assertEquals(
            "Unable to process the request. Please check your input and try again.",
            HttpErrorMessages.getMessage(422),
        )
        assertEquals(
            "Too many requests. Please wait a moment and try again.",
            HttpErrorMessages.getMessage(429),
        )
    }

    @Test
    fun getMessage_returnsExactMessage_forStandardServerErrors() {
        assertEquals(
            "The server encountered an error. Please try again later.",
            HttpErrorMessages.getMessage(500),
        )
        assertEquals(
            "Bad gateway. Received an invalid response from the upstream server.",
            HttpErrorMessages.getMessage(502),
        )
        assertEquals(
            "Service is temporarily unavailable. Maintenance may be in progress.",
            HttpErrorMessages.getMessage(503),
        )
        assertEquals(
            "Gateway timed out waiting for the server response.",
            HttpErrorMessages.getMessage(504),
        )
    }

    @Test
    fun getMessage_returnsGenericFallback_forUnmapped4xxAnd5xx() {
        assertTrue(HttpErrorMessages.getMessage(418).contains("HTTP 418"))
        assertTrue(HttpErrorMessages.getMessage(599).contains("HTTP 599"))
        assertTrue(HttpErrorMessages.getMessage(301).contains("HTTP 301"))
    }
}
