package govi.core.ui.component.nointernet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import govi.core.ui.theme.spacing

/**
 * Full-screen format for No Internet states.
 */
@Composable
fun NoInternetFullScreen(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenSettings: (() -> Unit)? = null,
    title: String = "You're Offline",
    message: String = "No internet connection detected. Please verify your Wi-Fi or mobile network and try again.",
    retryLabel: String = "Try Again",
    settingsLabel: String = "Network Settings",
) {
    val context = LocalContext.current
    val effectiveOpenSettings = onOpenSettings ?: { openWirelessSettings(context) }
    val spacing = MaterialTheme.spacing

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(44.dp),
                )
            }

            Spacer(modifier = Modifier.height(spacing.large))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = spacing.medium),
            )

            Spacer(modifier = Modifier.height(spacing.extraLarge))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                OutlinedButton(
                    onClick = effectiveOpenSettings,
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    Text(text = settingsLabel)
                }

                Spacer(modifier = Modifier.width(spacing.medium))

                Button(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f, fill = false),
                ) {
                    Text(text = retryLabel)
                }
            }
        }
    }
}

@Preview(name = "No Internet Full Screen", showBackground = true)
@Composable
private fun NoInternetFullScreenPreview() {
    MaterialTheme {
        NoInternetFullScreen(onRetry = {})
    }
}
