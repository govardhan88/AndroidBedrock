package com.govi.androidbedrock.core.network.error

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

/**
 * Parsed representation of a remote API error payload.
 */
data class ParsedErrorResponse(
    val message: String?,
    val code: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
)

/**
 * Robust JSON error response parser that safely extracts messages, error codes,
 * and validation field errors across different industry API response conventions.
 */
object RemoteErrorParser {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
        }

    @Serializable
    private data class StandardErrorDto(
        val message: String? = null,
        val error: String? = null,
        val detail: String? = null,
        val code: String? = null,
    )

    /**
     * Parses a raw error JSON string into a structured [ParsedErrorResponse].
     * Returns null if [rawJson] is null, blank, or not parseable as a JSON object.
     */
    fun parse(rawJson: String?): ParsedErrorResponse? {
        if (rawJson.isNullOrBlank()) return null

        return try {
            val rootElement = json.parseToJsonElement(rawJson.trim())
            if (rootElement !is JsonObject) return null

            // 1. Attempt standard DTO extraction
            val standardDto =
                try {
                    json.decodeFromJsonElement(StandardErrorDto.serializer(), rootElement)
                } catch (_: Throwable) {
                    null
                }

            val primaryMessage =
                standardDto?.message?.takeIf { it.isNotBlank() }
                    ?: standardDto?.detail?.takeIf { it.isNotBlank() }
                    ?: standardDto?.error?.takeIf { it.isNotBlank() }

            // 2. Attempt field error map extraction
            val fieldErrors = extractFieldErrors(rootElement)

            ParsedErrorResponse(
                message = primaryMessage,
                code = standardDto?.code,
                fieldErrors = fieldErrors,
            )
        } catch (_: Throwable) {
            // Not a valid JSON payload (e.g. HTML error page or raw plain text)
            // If it's a short single-line string, treat as message
            val trimmed = rawJson.trim()
            if (!trimmed.startsWith("<") && trimmed.length < 256) {
                ParsedErrorResponse(message = trimmed)
            } else {
                null
            }
        }
    }

    private fun extractFieldErrors(root: JsonObject): Map<String, String> {
        val errorsMap = mutableMapOf<String, String>()
        val errorsElement = root["errors"] ?: return emptyMap()

        try {
            when {
                // Scenario A: {"errors": {"email": "Invalid", "password": "Too short"}}
                // Scenario B: {"errors": {"email": ["Invalid", "Taken"]}}
                errorsElement is JsonObject -> {
                    for ((key, value) in errorsElement) {
                        when {
                            value is JsonObject -> Unit
                            value.toString().startsWith("[") -> {
                                val first =
                                    value.jsonArray
                                        .firstOrNull()
                                        ?.jsonPrimitive
                                        ?.content
                                if (!first.isNullOrBlank()) errorsMap[key] = first
                            }
                            else -> {
                                val content = value.jsonPrimitive.content
                                if (content.isNotBlank()) errorsMap[key] = content
                            }
                        }
                    }
                }
                // Scenario C: {"errors": [{"field": "email", "message": "Invalid email"}]}
                errorsElement.toString().startsWith("[") -> {
                    for (item in errorsElement.jsonArray) {
                        if (item is JsonObject) {
                            val field = item["field"]?.jsonPrimitive?.content
                            val msg = item["message"]?.jsonPrimitive?.content
                            if (!field.isNullOrBlank() && !msg.isNullOrBlank()) {
                                errorsMap[field] = msg
                            }
                        }
                    }
                }
            }
        } catch (_: Throwable) {
            // Fail gracefully
        }

        return errorsMap
    }
}
