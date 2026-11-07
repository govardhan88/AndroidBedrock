package govi.core.ui.component.nointernet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Standard Dialog format for No Internet alerts (the default choice).
 */
@Composable
fun NoInternetDialog(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    title: String = "No Internet Connection",
    message: String = "Please check your network settings and try again.",
    retryLabel: String = "Try Again",
    settingsLabel: String = "Network Settings",
    dismissLabel: String = "Dismiss",
) {
    val context = LocalContext.current
    val effectiveOpenSettings = onOpenSettings ?: { openWirelessSettings(context) }

    AlertDialog(
        onDismissRequest = { onDismiss?.invoke() },
        modifier = modifier,
        icon = {
            Box(
                modifier =
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(28.dp),
                )
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            Button(onClick = onRetry) {
                Text(text = retryLabel)
            }
        },
        dismissButton = {
            if (onDismiss != null) {
                TextButton(onClick = onDismiss) {
                    Text(text = dismissLabel)
                }
            } else {
                OutlinedButton(onClick = effectiveOpenSettings) {
                    Text(text = settingsLabel)
                }
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
    )
}

@Preview(name = "No Internet Dialog - Default", showBackground = true)
@Composable
private fun NoInternetDialogPreview() {
    MaterialTheme {
        NoInternetDialog(
            onRetry = {},
            onDismiss = {},
        )
    }
}
