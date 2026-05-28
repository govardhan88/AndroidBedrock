package com.govi.androidbedrock.core.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Extension function to safely launch a coroutine with error handling
 */
fun CoroutineScope.launchSafely(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    onError: (Exception) -> Unit = {},
    block: suspend () -> Unit
): Job {
    return launch(dispatcher) {
        try {
            block()
        } catch (e: Exception) {
            onError(e)
        }
    }
}

/**
 * Extension function to format error messages
 */
fun Exception.getReadableMessage(): String {
    return this.message ?: "An unexpected error occurred"
}

/**
 * Extension function to check if string is a valid email
 */
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    return this.matches(emailRegex)
}
