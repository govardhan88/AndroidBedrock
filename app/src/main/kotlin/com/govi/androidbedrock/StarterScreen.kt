package com.govi.androidbedrock

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.govi.androidbedrock.core.ui.component.badge.StatusBadge
import com.govi.androidbedrock.core.ui.component.badge.StatusVariant
import com.govi.androidbedrock.core.ui.component.bar.AppTopBar
import com.govi.androidbedrock.core.ui.component.button.SecondaryButton
import com.govi.androidbedrock.core.ui.component.card.AppCard
import com.govi.androidbedrock.core.ui.component.card.CardVariant
import com.govi.androidbedrock.core.ui.theme.spacing

/**
 * Starter screen displayed on fresh installations of the Android Bedrock base template.
 *
 * Provides a clean overview of foundational modules, quick demo navigation, and feature scaffolding instructions.
 */
@Composable
fun StarterScreen(
    onNavigateToDemo: () -> Unit = {},
    onOptionClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Android Bedrock")
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(MaterialTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        ) {
            // Hero card
            AppCard(variant = CardVariant.Elevated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Base Template Ready",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                        Text(
                            text = "Modular Clean Architecture for Android",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    StatusBadge(
                        text = "Clean State",
                        variant = StatusVariant.Success,
                    )
                }
            }

            // Interactive Demo Section
            Text(
                text = "Interactive Feature Demos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            AppCard(variant = CardVariant.Outlined) {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                    DemoOptionRow(
                        title = "1. Design System & UI Components",
                        desc = "Buttons with loading/disabled states, text fields, cards, and shimmer loaders",
                        badge = "Compose",
                        onClick = { onOptionClick("ui-components") },
                    )
                    DemoOptionRow(
                        title = "2. Network Resilience & Error Parsing",
                        desc = "Simulate HTTP 400-503, HTTP/2 empty reason fallback, and JSON error parser",
                        badge = "SafeApiCall",
                        onClick = { onOptionClick("network-resilience") },
                    )
                    DemoOptionRow(
                        title = "3. Connectivity Monitor & Offline Formats",
                        desc = "Live network detection, 4 offline UI formats (Dialog, Banner, BottomSheet, Snackbar)",
                        badge = "NetworkMonitor",
                        onClick = { onOptionClick("offline-detection") },
                    )
                    DemoOptionRow(
                        title = "4. Encrypted Token DataStore",
                        desc = "Persist, inspect, and clear auth access & refresh tokens with DataStore",
                        badge = "DataStore",
                        onClick = { onOptionClick("secure-datastore") },
                    )
                    DemoOptionRow(
                        title = "5. Analytics Tracker & Crash Reporting",
                        desc = "Structured event logging with parameters and non-fatal Crashlytics error reports",
                        badge = "Telemetry",
                        onClick = { onOptionClick("telemetry") },
                    )
                    DemoOptionRow(
                        title = "6. MVI Architecture & State Stream",
                        desc = "Unidirectional data flow: dispatch UiIntent, observe UiState, emit UiEffects",
                        badge = "MVI",
                        onClick = { onOptionClick("mvi-architecture") },
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))
                    SecondaryButton(
                        text = "Open Showcase Hub",
                        onClick = onNavigateToDemo,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Core modules section
            Text(
                text = "Preconfigured Core Modules",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            AppCard(variant = CardVariant.Outlined) {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                    ModuleItem(
                        name = ":core:base",
                        desc = "MVI BaseViewModel, Result contract, SafeApiCall, UseCase abstractions",
                        icon = Icons.Default.CheckCircle,
                    )
                    ModuleItem(
                        name = ":core:navigation",
                        desc = "Type-safe Compose Destinations, AppNavigator, FeatureEntry contracts",
                        icon = Icons.Default.CheckCircle,
                    )
                    ModuleItem(
                        name = ":core:network",
                        desc = "Retrofit, OkHttp, JSON serialization, dynamic Base URL & connectivity monitor",
                        icon = Icons.Default.CheckCircle,
                    )
                    ModuleItem(
                        name = ":core:datastore",
                        desc = "Preferences DataStore with encrypted secure storage support",
                        icon = Icons.Default.CheckCircle,
                    )
                    ModuleItem(
                        name = ":core:ui",
                        desc = "Material 3 design system, typography, color palettes, reusable components",
                        icon = Icons.Default.CheckCircle,
                    )
                }
            }

            // Feature scaffolding guide
            Text(
                text = "Scaffold New Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )

            AppCard(variant = CardVariant.Outlined) {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
                        Text(
                            text = "Automatic Scaffolding Commands",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Text(
                        text = "1. Clean Architecture (3 submodules - domain, data, presentation):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    CodeSnippetBox(command = "./gradlew createFeature -Pname=orders")

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))

                    Text(
                        text = "2. Compact Feature (single module):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    CodeSnippetBox(command = "./gradlew createFeature -Pname=settings -Ptype=compact")

                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraSmall))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
                        Text(
                            text = "Registers Dagger Hilt DI, AppRoute, and settings automatically.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoOptionRow(
    title: String,
    desc: String,
    badge: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = MaterialTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
            ) {
                StatusBadge(text = badge, variant = StatusVariant.Info)
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open $title",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun ModuleItem(
    name: String,
    desc: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp),
            )
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CodeSnippetBox(
    command: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = command,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
