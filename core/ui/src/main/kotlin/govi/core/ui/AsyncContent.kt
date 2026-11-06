package govi.core.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import govi.core.ui.component.ErrorView
import govi.core.ui.component.FullScreenLoadingView

/**
 * Reusable Jetpack Compose container that handles Loading, Error, and Success states of [AsyncState]
 * with seamless state crossfading and customizable content slots.
 */
@Composable
fun <T> AsyncContent(
    state: AsyncState<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loadingContent: @Composable BoxScope.() -> Unit = {
        FullScreenLoadingView()
    },
    errorContent: @Composable BoxScope.(message: String) -> Unit = { message ->
        ErrorView(
            message = message,
            onRetry = onRetry
        )
    },
    content: @Composable BoxScope.(data: T) -> Unit
) {
    Box(modifier = modifier) {
        Crossfade(
            targetState = state,
            label = "AsyncContentCrossfade"
        ) { targetState ->
            when (targetState) {
                is AsyncState.Loading -> loadingContent()
                is AsyncState.Error -> errorContent(targetState.message)
                is AsyncState.Success -> content(targetState.data)
                is AsyncState.Idle -> Unit
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AsyncContentLoadingPreview() {
    MaterialTheme {
        AsyncContent(
            state = AsyncState.Loading,
            onRetry = {},
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Success Data")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AsyncContentErrorPreview() {
    MaterialTheme {
        AsyncContent(
            state = AsyncState.Error("Failed to fetch records. Network unavailable."),
            onRetry = {},
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Success Data")
        }
    }
}
