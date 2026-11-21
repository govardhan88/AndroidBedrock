package com.govi.androidbedrock.feature.demo.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.govi.androidbedrock.core.ui.AsyncContent
import com.govi.androidbedrock.core.ui.AsyncState
import com.govi.androidbedrock.core.ui.component.EmptyStateView
import com.govi.androidbedrock.core.ui.component.badge.StatusBadge
import com.govi.androidbedrock.core.ui.component.badge.StatusVariant
import com.govi.androidbedrock.core.ui.component.bar.AppTopBar
import com.govi.androidbedrock.core.ui.component.card.AppCard
import com.govi.androidbedrock.core.ui.component.card.CardVariant
import com.govi.androidbedrock.core.ui.component.shimmer.ShimmerListItem
import com.govi.androidbedrock.core.ui.theme.spacing
import com.govi.androidbedrock.feature.demo.domain.model.DemoItem

/**
 * Stateful entry point for the Demo list screen.
 */
@Composable
fun DemoRoute(
    modifier: Modifier = Modifier,
    viewModel: DemoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DemoScreen(
        uiState = uiState,
        onOptionClick = { optionId -> viewModel.processIntent(DemoIntent.OptionClicked(optionId)) },
        onRetry = viewModel::loadItems,
        onBackClick = viewModel::onBackClicked,
        modifier = modifier,
    )
}

/**
 * Stateless Demo screen rendering the list of interactive options.
 */
@Composable
fun DemoScreen(
    uiState: DemoUiState,
    onOptionClick: (String) -> Unit,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Bedrock Showcase",
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
            DemoListContent(
                items = items,
                onOptionClick = onOptionClick,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DemoListContent(
    items: List<DemoItem>,
    onOptionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    if (items.isEmpty()) {
        EmptyStateView(
            title = "No Demo Available",
            message = "There are currently no items to display.",
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            item {
                AppCard(variant = CardVariant.Elevated) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(spacing.small))
                        Column {
                            Text(
                                text = "Interactive Showcase Hub",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "Tap any option below to inspect and test live Bedrock capabilities.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            items(items, key = { it.id }) { item ->
                AppCard(
                    variant = CardVariant.Outlined,
                    contentPadding = spacing.medium,
                    modifier = Modifier.clickable { onOptionClick(item.id) },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                            ) {
                                StatusBadge(
                                    text = item.badgeText,
                                    variant = StatusVariant.Info,
                                )
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Spacer(modifier = Modifier.height(spacing.extraSmall))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(spacing.extraSmall))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            if (item.features.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(spacing.small))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    item.features.take(3).forEach { feature ->
                                        SuggestionChip(
                                            onClick = { onOptionClick(item.id) },
                                            label = {
                                                Text(
                                                    text = feature,
                                                    style = MaterialTheme.typography.labelSmall,
                                                )
                                            },
                                            colors =
                                                SuggestionChipDefaults.suggestionChipColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                ),
                                        )
                                    }
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open ${item.title}",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier =
                                Modifier
                                    .padding(start = spacing.small)
                                    .size(20.dp),
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
private fun DemoScreenSuccessPreview() {
    MaterialTheme {
        DemoScreen(
            uiState =
                DemoUiState(
                    itemsState =
                        AsyncState.Success(
                            listOf(
                                DemoItem(
                                    id = "ui-components",
                                    title = "Design System & UI Components",
                                    description = "Buttons, text fields, cards, and shimmer loaders.",
                                    category = "Presentation",
                                    badgeText = "Compose",
                                    features = listOf("AppButton", "AppTextField", "StatusBadge"),
                                ),
                            ),
                        ),
                ),
            onOptionClick = {},
            onRetry = {},
            onBackClick = {},
        )
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
private fun DemoScreenLoadingPreview() {
    MaterialTheme {
        DemoScreen(
            uiState = DemoUiState(itemsState = AsyncState.Loading),
            onOptionClick = {},
            onRetry = {},
            onBackClick = {},
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun DemoScreenErrorPreview() {
    MaterialTheme {
        DemoScreen(
            uiState =
                DemoUiState(
                    itemsState = AsyncState.Error("Failed to fetch demo details."),
                ),
            onOptionClick = {},
            onRetry = {},
            onBackClick = {},
        )
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun DemoScreenEmptyPreview() {
    MaterialTheme {
        DemoScreen(
            uiState = DemoUiState(itemsState = AsyncState.Success(emptyList())),
            onOptionClick = {},
            onRetry = {},
            onBackClick = {},
        )
    }
}
