package com.govi.androidbedrock.core.ui.component.nointernet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * Declarative container for effortlessly managing No Internet states across any screen or app-wide.
 *
 * Supports all design formats: [NoInternetFormat.Dialog] (default), [NoInternetFormat.FullScreen],
 * [NoInternetFormat.BottomSheet], [NoInternetFormat.Banner], and [NoInternetFormat.Snackbar].
 *
 * @param isOffline Whether the device is currently disconnected from the internet.
 * @param modifier Modifier applied to the outer layout.
 * @param format The presentation format choice. Defaults to [NoInternetFormat.Dialog].
 * @param title Title displayed in dialog, fullscreen, and bottomsheet formats.
 * @param message Supportive explanation text.
 * @param retryLabel Label for the primary retry CTA.
 * @param settingsLabel Label for the system settings button.
 * @param isDismissible Whether the user can temporarily dismiss the alert (dialog/bottomsheet).
 * @param onRetry Callback invoked when the user taps the retry CTA.
 * @param onDismiss Optional callback invoked when the user dismisses the dialog/bottomsheet.
 * @param onOpenSettings Optional callback to launch settings (defaults to opening Android Wireless Settings).
 * @param content The underlying screen composable to display.
 */
@Composable
fun NoInternetHost(
    isOffline: Boolean,
    modifier: Modifier = Modifier,
    format: NoInternetFormat = NoInternetFormat.Dialog,
    title: String = "No Internet Connection",
    message: String = "Please check your network settings and try again.",
    retryLabel: String = "Try Again",
    settingsLabel: String = "Network Settings",
    isDismissible: Boolean = true,
    onRetry: () -> Unit = {},
    onDismiss: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var isDismissed by remember { mutableStateOf(false) }

    LaunchedEffect(isOffline) {
        if (!isOffline) {
            isDismissed = false
        }
    }

    val shouldShow = isOffline && (!isDismissed || !isDismissible)

    when (format) {
        NoInternetFormat.Dialog -> {
            content()
            if (shouldShow) {
                NoInternetDialog(
                    onRetry = onRetry,
                    onDismiss =
                        if (isDismissible) {
                            {
                                isDismissed = true
                                onDismiss?.invoke()
                            }
                        } else {
                            null
                        },
                    onOpenSettings = onOpenSettings,
                    title = title,
                    message = message,
                    retryLabel = retryLabel,
                    settingsLabel = settingsLabel,
                )
            }
        }

        NoInternetFormat.FullScreen -> {
            if (shouldShow) {
                NoInternetFullScreen(
                    onRetry = onRetry,
                    onOpenSettings = onOpenSettings,
                    title = title,
                    message = message,
                    retryLabel = retryLabel,
                    settingsLabel = settingsLabel,
                    modifier = modifier,
                )
            } else {
                content()
            }
        }

        NoInternetFormat.BottomSheet -> {
            content()
            if (shouldShow) {
                NoInternetBottomSheet(
                    onRetry = onRetry,
                    onDismissRequest = {
                        if (isDismissible) {
                            isDismissed = true
                            onDismiss?.invoke()
                        }
                    },
                    onOpenSettings = onOpenSettings,
                    title = title,
                    message = message,
                    retryLabel = retryLabel,
                    settingsLabel = settingsLabel,
                )
            }
        }

        NoInternetFormat.Banner -> {
            Column(modifier = modifier.fillMaxSize()) {
                NoInternetBanner(
                    isVisible = isOffline,
                    onRetry = onRetry,
                    message = message,
                    retryLabel = retryLabel,
                )
                Box(modifier = Modifier.weight(1f)) {
                    content()
                }
            }
        }

        NoInternetFormat.Snackbar -> {
            Box(modifier = modifier.fillMaxSize()) {
                content()
                NoInternetSnackbar(
                    isVisible = isOffline,
                    onRetry = onRetry,
                    message = message,
                    retryLabel = retryLabel,
                )
            }
        }
    }
}

@Preview(name = "No Internet Host - Dialog (Default)", showBackground = true)
@Composable
private fun NoInternetHostDialogPreview() {
    MaterialTheme {
        NoInternetHost(
            isOffline = true,
            format = NoInternetFormat.Dialog,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "Sample Screen Content")
            }
        }
    }
}

@Preview(name = "No Internet Host - Banner", showBackground = true)
@Composable
private fun NoInternetHostBannerPreview() {
    MaterialTheme {
        NoInternetHost(
            isOffline = true,
            format = NoInternetFormat.Banner,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "Sample Screen Content")
            }
        }
    }
}
