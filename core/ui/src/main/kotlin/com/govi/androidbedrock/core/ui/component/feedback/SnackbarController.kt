package com.govi.androidbedrock.core.ui.component.feedback

import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackbarAction(
    val name: String,
    val action: () -> Unit,
)

data class SnackbarEvent(
    val message: String,
    val action: SnackbarAction? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
)

/**
 * Event bus for dispatching Snackbars from ViewModels without UI coupling.
 *
 * Example in ViewModel:
 * ```kotlin
 * SnackbarController.sendEvent(SnackbarEvent("Settings updated"))
 * ```
 */
object SnackbarController {
    private val _events = Channel<SnackbarEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }

    fun trySendEvent(event: SnackbarEvent) {
        _events.trySend(event)
    }
}
