package com.govi.androidbedrock.core.ui.component.nointernet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.govi.androidbedrock.core.ui.theme.spacing

/**
 * Modal Bottom Sheet format for No Internet notifications.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoInternetBottomSheet(
    onRetry: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenSettings: (() -> Unit)? = null,
    title: String = "No Connection",
    message: String = "Your device is not connected to the internet. Please check your network and try again.",
    retryLabel: String = "Try Again",
    settingsLabel: String = "Settings",
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val effectiveOpenSettings = onOpenSettings ?: { openWirelessSettings(context) }
    val spacing = MaterialTheme.spacing

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.large)
                    .padding(bottom = spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(32.dp),
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(spacing.large))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                OutlinedButton(
                    onClick = effectiveOpenSettings,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = settingsLabel)
                }

                Button(
                    onClick = onRetry,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = retryLabel)
                }
            }
        }
    }
}

@Preview(name = "No Internet Bottom Sheet", showBackground = true)
@Composable
private fun NoInternetBottomSheetPreview() {
    MaterialTheme {
        NoInternetBottomSheet(
            onRetry = {},
            onDismissRequest = {},
        )
    }
}
