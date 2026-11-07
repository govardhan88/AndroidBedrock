package govi.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import govi.core.ui.AsyncContent
import govi.core.ui.component.EmptyStateView
import govi.core.ui.component.bar.AppTopBar
import govi.core.ui.component.shimmer.ShimmerListItem
import govi.feature.settings.domain.model.SettingsItem

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Settings",
                onBackClick = viewModel::onBackClicked,
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        AsyncContent(
            state = uiState.itemsState,
            onRetry = viewModel::loadItems,
            loadingContent = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
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
    if (items.isEmpty()) {
        EmptyStateView(
            title = "No Settings Available",
            message = "There are currently no items to display.",
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items, key = { it.id }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        if (item.description.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
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
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsListContent(
            items =
                listOf(
                    SettingsItem("1", "Sample Item 1", "Description for item 1"),
                    SettingsItem("2", "Sample Item 2", "Description for item 2"),
                ),
        )
    }
}
