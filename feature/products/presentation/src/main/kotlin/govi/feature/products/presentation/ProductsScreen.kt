package govi.feature.products.presentation

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
import govi.feature.products.domain.model.ProductsItem

/**
 * Stateful entry point for the Products screen.
 *
 * Connects the [ProductsViewModel] to the stateless [ProductsScreen] composable.
 */
@Composable
fun ProductsRoute(
    modifier: Modifier = Modifier,
    viewModel: ProductsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProductsScreen(
        uiState = uiState,
        onRetry = viewModel::loadItems,
        onBackClick = viewModel::onBackClicked,
        modifier = modifier,
    )
}

/**
 * Stateless Products screen rendering UI states (Loading, Success, Error, Empty).
 *
 * Free from Android/Hilt dependencies for instant Compose Previews and deterministic UI testing.
 */
@Composable
fun ProductsScreen(
    uiState: ProductsUiState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Products",
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
            ProductsListContent(items = items)
        }
    }
}

@Composable
fun ProductsListContent(
    items: List<ProductsItem>,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    if (items.isEmpty()) {
        EmptyStateView(
            title = "No Products Available",
            message = "There are currently no products in the catalog.",
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
private fun ProductsScreenSuccessPreview() {
    MaterialTheme {
        ProductsScreen(
            uiState =
                ProductsUiState(
                    itemsState =
                        AsyncState.Success(
                            listOf(
                                ProductsItem("1", "Smartphone", "Latest generation flagship"),
                                ProductsItem("2", "Wireless Earbuds", "Active noise cancellation"),
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
private fun ProductsScreenLoadingPreview() {
    MaterialTheme {
        ProductsScreen(
            uiState = ProductsUiState(itemsState = AsyncState.Loading),
            onRetry = {},
            onBackClick = {},
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun ProductsScreenErrorPreview() {
    MaterialTheme {
        ProductsScreen(
            uiState =
                ProductsUiState(
                    itemsState = AsyncState.Error("Failed to fetch product catalog."),
                ),
            onRetry = {},
            onBackClick = {},
        )
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun ProductsScreenEmptyPreview() {
    MaterialTheme {
        ProductsScreen(
            uiState = ProductsUiState(itemsState = AsyncState.Success(emptyList())),
            onRetry = {},
            onBackClick = {},
        )
    }
}
