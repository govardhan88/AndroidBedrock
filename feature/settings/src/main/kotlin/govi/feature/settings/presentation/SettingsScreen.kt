package govi.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import govi.core.ui.AsyncContent
import govi.core.ui.AsyncState
import govi.core.ui.component.EmptyStateView
import govi.core.ui.component.bar.AppTopBar
import govi.core.ui.component.card.AppCard
import govi.core.ui.component.card.CardVariant
import govi.core.ui.component.shimmer.ShimmerListItem
import govi.core.ui.theme.spacing
import govi.feature.settings.domain.model.SettingsItem

/**
 * Stateful entry point for the Settings screen.
 *
 * Connects the [SettingsViewModel] to the stateless [SettingsScreen] composable.
 */
@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onRetry = viewModel::loadItems,
        onBackClick = viewModel::onBackClicked,
        modifier = modifier,
    )
}

/**
 * Stateless Settings screen rendering UI states (Loading, Success, Error, Empty).
 *
 * Free from Android/Hilt dependencies for instant Compose Previews and deterministic UI testing.
 */
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Settings",
                onBackClick = onBackClick,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        AsyncContent(
            state = uiState.itemsState,
            onRetry = onRetry,
            loadingContent = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.small),
                ) {
                    repeat(4) { ShimmerListItem() }
                }
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) { items ->
            SettingsListContent(items = items)
        }
    }
}

@Composable
fun SettingsListContent(
    items: List<SettingsItem>,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    if (items.isEmpty()) {
        EmptyStateView(
            title = "No Settings Available",
            message = "There are currently no items to display.",
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            items(items, key = { it.id }) { item ->
                AppCard(
                    variant = CardVariant.Outlined,
                    contentPadding = spacing.medium,
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    if (item.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(spacing.extraSmall))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// Multi-State Compose Previews
// ==========================================

@Preview(name = "Success State", showBackground = true)
@Composable
private fun SettingsScreenSuccessPreview() {
    MaterialTheme {
        SettingsScreen(
            uiState =
                SettingsUiState(
                    itemsState =
                        AsyncState.Success(
                            listOf(
                                SettingsItem("1", "Notifications", "Manage push notifications and alerts"),
                                SettingsItem("2", "Dark Mode", "Toggle light and dark appearance"),
                            ),
                        ),
                ),
            onRetry = {},
            onBackClick = {},
        )
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
private fun SettingsScreenLoadingPreview() {
    MaterialTheme {
        SettingsScreen(
            uiState = SettingsUiState(itemsState = AsyncState.Loading),
            onRetry = {},
            onBackClick = {},
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun SettingsScreenErrorPreview() {
    MaterialTheme {
        SettingsScreen(
            uiState =
                SettingsUiState(
                    itemsState = AsyncState.Error("Failed to load settings from server."),
                ),
            onRetry = {},
            onBackClick = {},
        )
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun SettingsScreenEmptyPreview() {
    MaterialTheme {
        SettingsScreen(
            uiState = SettingsUiState(itemsState = AsyncState.Success(emptyList())),
            onRetry = {},
            onBackClick = {},
        )
    }
}
