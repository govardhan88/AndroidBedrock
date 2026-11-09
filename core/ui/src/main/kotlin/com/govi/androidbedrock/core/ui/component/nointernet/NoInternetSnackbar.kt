package com.govi.androidbedrock.core.ui.component.nointernet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.govi.androidbedrock.core.ui.theme.spacing

/**
 * Floating Snackbar / Toast format for No Internet alerts.
 */
@Composable
fun NoInternetSnackbar(
    isVisible: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    message: String = "You are currently offline",
    retryLabel: String = "Retry",
) {
    val spacing = MaterialTheme.spacing

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            Snackbar(
                modifier = Modifier.padding(spacing.medium),
                action = {
                    TextButton(onClick = onRetry) {
                        Text(
                            text = retryLabel,
                            color = MaterialTheme.colorScheme.inversePrimary,
                        )
                    }
                },
                dismissAction = null,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                    Text(
                        text = message,
                        modifier = Modifier.padding(start = spacing.small),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Preview(name = "No Internet Snackbar", showBackground = true)
@Composable
private fun NoInternetSnackbarPreview() {
    MaterialTheme {
        NoInternetSnackbar(
            isVisible = true,
            onRetry = {},
        )
    }
}
