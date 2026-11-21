package com.govi.androidbedrock.feature.demo.presentation.detail

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.govi.androidbedrock.core.ui.component.badge.StatusBadge
import com.govi.androidbedrock.core.ui.component.badge.StatusVariant
import com.govi.androidbedrock.core.ui.component.bar.AppTopBar
import com.govi.androidbedrock.core.ui.component.button.AppOutlinedButton
import com.govi.androidbedrock.core.ui.component.button.PrimaryButton
import com.govi.androidbedrock.core.ui.component.button.SecondaryButton
import com.govi.androidbedrock.core.ui.component.card.AppCard
import com.govi.androidbedrock.core.ui.component.card.CardVariant
import com.govi.androidbedrock.core.ui.component.input.AppPasswordTextField
import com.govi.androidbedrock.core.ui.component.input.AppTextField
import com.govi.androidbedrock.core.ui.component.nointernet.NoInternetBanner
import com.govi.androidbedrock.core.ui.component.nointernet.NoInternetBottomSheet
import com.govi.androidbedrock.core.ui.component.nointernet.NoInternetDialog
import com.govi.androidbedrock.core.ui.component.nointernet.NoInternetSnackbar
import com.govi.androidbedrock.core.ui.component.nointernet.openWirelessSettings
import com.govi.androidbedrock.core.ui.component.shimmer.ShimmerListItem
import com.govi.androidbedrock.core.ui.theme.spacing
import com.govi.androidbedrock.core.ui.util.ObserveAsEvents
import kotlinx.coroutines.launch

@Composable
fun DemoDetailRoute(
    modifier: Modifier = Modifier,
    viewModel: DemoDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isOnline by viewModel.networkMonitor.isOnline.collectAsStateWithLifecycle(
        initialValue = viewModel.networkMonitor.isCurrentlyOnline,
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.effect) { effect ->
        when (effect) {
            is DemoDetailEffect.NavigateBack -> viewModel.onBackClicked()
            is DemoDetailEffect.ShowSnackbar -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    DemoDetailScreen(
        uiState = uiState,
        isOnline = isOnline,
        snackbarHostState = snackbarHostState,
        context = context,
        onIntent = viewModel::processIntent,
        modifier = modifier,
    )
}

@Composable
fun DemoDetailScreen(
    uiState: DemoDetailUiState,
    isOnline: Boolean,
    snackbarHostState: SnackbarHostState,
    context: Context,
    onIntent: (DemoDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val title = uiState.demoItem?.title ?: "Demo Details"

    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                onBackClick = { onIntent(DemoDetailIntent.BackClicked) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            // Header card
            uiState.demoItem?.let { item ->
                AppCard(variant = CardVariant.Elevated) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                            StatusBadge(
                                text = item.badgeText,
                                variant = StatusVariant.Info,
                            )
                        }
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Option-specific interactive demo section
            when (uiState.optionId) {
                "ui-components" -> UiComponentsDemoSection(uiState = uiState, onIntent = onIntent)
                "network-resilience" -> NetworkResilienceDemoSection(uiState = uiState, onIntent = onIntent)
                "offline-detection" ->
                    OfflineDetectionDemoSection(
                        uiState = uiState,
                        isOnline = isOnline,
                        context = context,
                        onIntent = onIntent,
                    )
                "secure-datastore" -> SecureDataStoreDemoSection(uiState = uiState, onIntent = onIntent)
                "telemetry" -> TelemetryDemoSection(uiState = uiState, onIntent = onIntent)
                "mvi-architecture" -> MviArchitectureDemoSection(uiState = uiState, onIntent = onIntent)
                else -> GenericDemoSection(uiState = uiState)
            }
        }
    }
}

// -------------------------------------------------------------------------
// Section 1: UI Components Demo
// -------------------------------------------------------------------------
@Composable
private fun UiComponentsDemoSection(
    uiState: DemoDetailUiState,
    onIntent: (DemoDetailIntent) -> Unit,
) {
    val spacing = MaterialTheme.spacing

    Text(
        text = "Interactive Component Controls",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )

    AppCard(variant = CardVariant.Outlined) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(
                text = "1. Button State & Variants",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                PrimaryButton(
                    text = "Clicks: ${uiState.buttonClickCount}",
                    onClick = { onIntent(DemoDetailIntent.IncrementButtonClick) },
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = "Toggle Loading",
                    onClick = { onIntent(DemoDetailIntent.ToggleButtonLoading) },
                    isLoading = uiState.isButtonLoading,
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                AppOutlinedButton(
                    text = "Outlined Button",
                    onClick = { onIntent(DemoDetailIntent.IncrementButtonClick) },
                    modifier = Modifier.weight(1f),
                )
                PrimaryButton(
                    text = "Disabled Button",
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    AppCard(variant = CardVariant.Outlined) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(
                text = "2. Form Inputs with Validation",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            AppTextField(
                value = uiState.inputSampleText,
                onValueChange = { onIntent(DemoDetailIntent.UpdateInputText(it)) },
                label = "Username / Email",
                placeholder = "Enter your username",
                modifier = Modifier.fillMaxWidth(),
            )
            AppPasswordTextField(
                value = uiState.passwordSampleText,
                onValueChange = { onIntent(DemoDetailIntent.UpdatePasswordText(it)) },
                label = "Secure Password",
                placeholder = "Enter password",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    AppCard(variant = CardVariant.Outlined) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(
                text = "3. Status Badges",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatusBadge(text = "Success", variant = StatusVariant.Success)
                StatusBadge(text = "Warning", variant = StatusVariant.Warning)
                StatusBadge(text = "Error", variant = StatusVariant.Error)
                StatusBadge(text = "Info", variant = StatusVariant.Info)
            }
        }
    }

    AppCard(variant = CardVariant.Outlined) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "4. Shimmer Skeleton Loader",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                AppOutlinedButton(
                    text = if (uiState.isShimmerActive) "Stop" else "Start Shimmer",
                    onClick = { onIntent(DemoDetailIntent.ToggleShimmer) },
                )
            }
            if (uiState.isShimmerActive) {
                ShimmerListItem()
            } else {
                Text(
                    text = "Tap 'Start Shimmer' above to view the animated skeleton placeholder.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// Section 2: Network Resilience & Error Parsing Demo
// -------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NetworkResilienceDemoSection(
    uiState: DemoDetailUiState,
    onIntent: (DemoDetailIntent) -> Unit,
) {
    val spacing = MaterialTheme.spacing
    val httpCodes = listOf(400, 401, 403, 404, 409, 429, 500, 503)

    Text(
        text = "HTTP Error Code Simulator",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )

    AppCard(variant = CardVariant.Outlined) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(
                text = "Select an HTTP status code to verify Bedrock's mapper & fallback:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                httpCodes.forEach { code ->
                    SuggestionChip(
                        onClick = { onIntent(DemoDetailIntent.SimulateHttpCode(code)) },
                        label = { Text("HTTP $code") },
                    )
                }
            }
        }
    }

    AppCard(variant = CardVariant.Elevated) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Simulated Response: HTTP ${uiState.simulatedHttpCode}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                StatusBadge(
                    text = if (uiState.simulatedHttpCode >= 500) "Server Error" else "Client Error",
                    variant = if (uiState.simulatedHttpCode >= 500) StatusVariant.Error else StatusVariant.Warning,
                )
            }
            Text(
                text = "Parsed Error Type: ${uiState.simulatedParsedError.ifEmpty { "AppError.Validation" }}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary,
            )
            val defaultMsg = "Select an HTTP code above to simulate."
            Text(
                text = "User-Facing Message: \"${uiState.simulatedErrorMessage.ifEmpty { defaultMsg }}\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
    }

    AppCard(variant = CardVariant.Outlined) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(
                text = "Backend JSON Error Parser Test",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            SecondaryButton(
                text = "Parse Sample JSON Error",
                onClick = {
                    onIntent(
                        DemoDetailIntent.SimulateJsonError(
                            """{"detail":"Account locked: Too many failed login attempts","code":"AUTH_LOCK"}""",
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// -------------------------------------------------------------------------
// Section 3: Offline Detection & Formats Demo
// -------------------------------------------------------------------------
@Composable
private fun OfflineDetectionDemoSection(
    uiState: DemoDetailUiState,
    isOnline: Boolean,
    context: Context,
    onIntent: (DemoDetailIntent) -> Unit,
) {
    val spacing = MaterialTheme.spacing

    Text(
        text = "Real-Time Connectivity Monitor",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )

    AppCard(variant = CardVariant.Elevated) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Current Network State",
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = if (isOnline) "Device is connected to the Internet" else "No connection detected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            StatusBadge(
                text = if (isOnline) "ONLINE" else "OFFLINE",
                variant = if (isOnline) StatusVariant.Success else StatusVariant.Error,
            )
        }
    }

    AppCard(variant = CardVariant.Outlined) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Text(
                text = "Interactive Offline UI Previews",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
            ) {
                AppOutlinedButton(
                    text = "Banner",
                    onClick = { onIntent(DemoDetailIntent.SelectOfflineFormat("Banner")) },
                    modifier = Modifier.weight(1f),
                )
                AppOutlinedButton(
                    text = "Dialog",
                    onClick = { onIntent(DemoDetailIntent.SelectOfflineFormat("Dialog")) },
                    modifier = Modifier.weight(1f),
                )
                AppOutlinedButton(
                    text = "Bottom Sheet",
                    onClick = { onIntent(DemoDetailIntent.SelectOfflineFormat("BottomSheet")) },
                    modifier = Modifier.weight(1f),
                )
                AppOutlinedButton(
                    text = "Snackbar",
                    onClick = { onIntent(DemoDetailIntent.SelectOfflineFormat("Snackbar")) },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(spacing.extraSmall))
            SecondaryButton(
                text = "Open Wireless Settings Intent",
                onClick = { openWirelessSettings(context) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    when (uiState.activeOfflineFormat) {
        "Banner" -> {
            NoInternetBanner(
                isVisible = true,
                onRetry = { onIntent(DemoDetailIntent.SelectOfflineFormat("None")) },
            )
        }
        "Dialog" -> {
            NoInternetDialog(
                onDismiss = { onIntent(DemoDetailIntent.SelectOfflineFormat("None")) },
                onRetry = { onIntent(DemoDetailIntent.SelectOfflineFormat("None")) },
            )
        }
        "BottomSheet" -> {
            NoInternetBottomSheet(
                onDismissRequest = { onIntent(DemoDetailIntent.SelectOfflineFormat("None")) },
                onRetry = { onIntent(DemoDetailIntent.SelectOfflineFormat("None")) },
            )
        }
        "Snackbar" -> {
            NoInternetSnackbar(
                isVisible = true,
                onRetry = { onIntent(DemoDetailIntent.SelectOfflineFormat("None")) },
            )
        }
    }
}

// -------------------------------------------------------------------------
// Section 4: Secure DataStore Demo
// -------------------------------------------------------------------------
@Composable
private fun SecureDataStoreDemoSection(
    uiState: DemoDetailUiState,
    onIntent: (DemoDetailIntent) -> Unit,
) {
    val spacing = MaterialTheme.spacing

    Text(
        text = "Token DataStore Operations",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )

    AppCard(variant = CardVariant.Outlined) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            AppTextField(
                value = uiState.inputAccessToken,
                onValueChange = {
                    onIntent(DemoDetailIntent.UpdateTokenInputs(it, uiState.inputRefreshToken))
                },
                label = "Access Token",
                modifier = Modifier.fillMaxWidth(),
            )
            AppTextField(
                value = uiState.inputRefreshToken,
                onValueChange = {
                    onIntent(DemoDetailIntent.UpdateTokenInputs(uiState.inputAccessToken, it))
                },
                label = "Refresh Token",
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                PrimaryButton(
                    text = "Save Token",
                    onClick = { onIntent(DemoDetailIntent.SaveTokenToDataStore) },
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = "Clear Token",
                    onClick = { onIntent(DemoDetailIntent.ClearTokenFromDataStore) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    AppCard(variant = CardVariant.Elevated) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
            Text(
                text = "Persisted DataStore State",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = uiState.currentSavedToken ?: "Loading...",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// -------------------------------------------------------------------------
// Section 5: Telemetry (Analytics & Crashlytics) Demo
// -------------------------------------------------------------------------
@Composable
private fun TelemetryDemoSection(
    uiState: DemoDetailUiState,
    onIntent: (DemoDetailIntent) -> Unit,
) {
    val spacing = MaterialTheme.spacing

    Text(
        text = "Dispatch Telemetry Events",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )

    AppCard(variant = CardVariant.Outlined) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                PrimaryButton(
                    text = "Log Analytics Event",
                    onClick = {
                        onIntent(
                            DemoDetailIntent.LogAnalyticsEvent(
                                name = "demo_option_tested",
                                params = mapOf("feature" to "telemetry", "timestamp" to System.currentTimeMillis().toString()),
                            ),
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = "Record Crashlytics Error",
                    onClick = {
                        onIntent(DemoDetailIntent.RecordCrashlyticsError("Simulated non-fatal diagnostic error"))
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    AppCard(variant = CardVariant.Elevated) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
            Text(
                text = "Session Telemetry Log (${uiState.telemetryLog.size})",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            if (uiState.telemetryLog.isEmpty()) {
                Text(
                    text = "No events logged yet. Tap the buttons above to record events.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                uiState.telemetryLog.take(6).forEach { entry ->
                    Text(
                        text = entry,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Section 6: MVI Architecture Demo
// -------------------------------------------------------------------------
@Composable
private fun MviArchitectureDemoSection(
    uiState: DemoDetailUiState,
    onIntent: (DemoDetailIntent) -> Unit,
) {
    val spacing = MaterialTheme.spacing

    Text(
        text = "Unidirectional MVI Loop",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )

    AppCard(variant = CardVariant.Elevated) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Current UiState Counter: ${uiState.mviCounter}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (uiState.isMviAsyncRunning) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            }
            Text(
                text =
                    "Every button click dispatches a sealed UiIntent. The ViewModel processes the intent, " +
                        "updates the immutable StateFlow<UiState>, and emits one-off UiEffects.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
            ) {
                PrimaryButton(
                    text = "Dispatch Intent (+1)",
                    onClick = { onIntent(DemoDetailIntent.IncrementMviCounter) },
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = "Async Task (+10)",
                    onClick = { onIntent(DemoDetailIntent.TriggerMviAsync) },
                    isLoading = uiState.isMviAsyncRunning,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun GenericDemoSection(uiState: DemoDetailUiState) {
    AppCard(variant = CardVariant.Elevated) {
        Text(
            text = "Demo Option ID: ${uiState.optionId}",
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
