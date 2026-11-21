package com.govi.androidbedrock.core.network.error

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class RemoteErrorParserTest {
    @Test
    fun parse_returnsNull_whenPayloadIsNullOrBlank() {
        assertNull(RemoteErrorParser.parse(null))
        assertNull(RemoteErrorParser.parse(""))
        assertNull(RemoteErrorParser.parse("   "))
    }

    @Test
    fun parse_extractsMessage_fromStandardMessageFormat() {
        val json = """{"message": "Invalid credentials", "code": "AUTH_FAILED"}"""
        val result = RemoteErrorParser.parse(json)

        assertNotNull(result)
        assertEquals("Invalid credentials", result?.message)
        assertEquals("AUTH_FAILED", result?.code)
    }

    @Test
    fun parse_extractsDetail_fromFastApiOrDjangoFormat() {
        val json = """{"detail": "Token has expired"}"""
        val result = RemoteErrorParser.parse(json)

        assertNotNull(result)
        assertEquals("Token has expired", result?.message)
    }

    @Test
    fun parse_extractsError_fromSpringOrOauthFormat() {
        val json = """{"error": "Unauthorized client"}"""
        val result = RemoteErrorParser.parse(json)

        assertNotNull(result)
        assertEquals("Unauthorized client", result?.message)
    }

    @Test
    fun parse_extractsFieldErrors_fromMapFormat() {
        val json =
            """
            {
                "message": "Validation failed",
                "errors": {
                    "email": "Email address already in use",
                    "password": "Password must be at least 8 characters"
                }
            }
            """.trimIndent()
        val result = RemoteErrorParser.parse(json)

        assertNotNull(result)
        assertEquals("Validation failed", result?.message)
        assertEquals("Email address already in use", result?.fieldErrors?.get("email"))
        assertEquals("Password must be at least 8 characters", result?.fieldErrors?.get("password"))
    }

    @Test
    fun parse_extractsFieldErrors_fromArrayErrorsFormat() {
        val json =
            """
            {
                "message": "Validation failed",
                "errors": {
                    "email": ["Email format invalid", "Must contain @"]
                }
            }
            """.trimIndent()
        val result = RemoteErrorParser.parse(json)

        assertNotNull(result)
        assertEquals("Email format invalid", result?.fieldErrors?.get("email"))
    }

    @Test
    fun parse_extractsFieldErrors_fromListFormat() {
        val json =
            """
            {
                "message": "Validation failed",
                "errors": [
                    {"field": "username", "message": "Username is required"}
                ]
            }
            """.trimIndent()
        val result = RemoteErrorParser.parse(json)

        assertNotNull(result)
        assertEquals("Username is required", result?.fieldErrors?.get("username"))
    }

    @Test
    fun parse_handlesPlainText_gracefully() {
        val plainText = "Service temporary outage"
        val result = RemoteErrorParser.parse(plainText)

        assertNotNull(result)
        assertEquals("Service temporary outage", result?.message)
    }

    @Test
    fun parse_ignoresHtmlErrorPages() {
        val html = "<html><body>502 Bad Gateway</body></html>"
        val result = RemoteErrorParser.parse(html)

        assertNull(result)
    }
}
