package govi.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Reusable Jetpack Compose container that handles Loading, Error, and Success states of [AsyncState].
 */
@Composable
fun <T> AsyncContent(
    state: AsyncState<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loadingContent: @Composable BoxScope.() -> Unit = {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    },
    errorContent: @Composable BoxScope.(message: String) -> Unit = { message ->
        DefaultErrorContent(message = message, onRetry = onRetry)
    },
    content: @Composable BoxScope.(data: T) -> Unit
) {
    Box(modifier = modifier) {
        when (state) {
            is AsyncState.Loading -> loadingContent()
            is AsyncState.Error -> errorContent(state.message)
            is AsyncState.Success -> content(state.data)
            is AsyncState.Idle -> Unit
        }
    }
}

/**
 * Default error presentation with message and retry button.
 */
@Composable
fun BoxScope.DefaultErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .align(Alignment.Center)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AsyncContentLoadingPreview() {
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
fun AsyncContentErrorPreview() {
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
