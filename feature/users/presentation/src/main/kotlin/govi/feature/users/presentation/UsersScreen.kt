package govi.feature.users.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import govi.core.ui.AsyncContent
import govi.core.ui.AsyncState
import govi.core.ui.component.EmptyStateView
import govi.core.ui.component.bar.AppTopBar
import govi.core.ui.component.card.AppCard
import govi.core.ui.component.card.AppListItem
import govi.core.ui.component.card.CardVariant
import govi.core.ui.component.shimmer.ShimmerListItem
import govi.core.ui.theme.spacing
import govi.feature.users.domain.model.User

/**
 * Stateful entry point for the Users screen.
 *
 * Connects the [UsersViewModel] to the stateless [UsersScreen] composable.
 */
@Composable
fun UsersRoute(
    modifier: Modifier = Modifier,
    viewModel: UsersViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UsersScreen(
        uiState = uiState,
        onRetry = viewModel::loadUsers,
        onUserClick = viewModel::onUserClicked,
        modifier = modifier,
    )
}

/**
 * Stateless Users screen rendering UI states (Loading, Success, Error, Empty).
 *
 * Free from Android/Hilt dependencies for instant Compose Previews and deterministic UI testing.
 */
@Composable
fun UsersScreen(
    uiState: UsersUiState,
    onRetry: () -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Scaffold(
        topBar = {
            AppTopBar(title = "Users Directory")
        },
        modifier = modifier.fillMaxSize(),
    ) { innerPadding ->
        AsyncContent(
            state = uiState.usersState,
            onRetry = onRetry,
            loadingContent = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.small),
                ) {
                    repeat(5) { ShimmerListItem() }
                }
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) { users ->
            UsersListContent(
                users = users,
                onUserClick = onUserClick,
            )
        }
    }
}

@Composable
fun UsersListContent(
    users: List<User>,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    if (users.isEmpty()) {
        EmptyStateView(
            title = "No Users Found",
            message = "There are no users registered in the system yet.",
        )
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            items(users, key = { it.id }) { user ->
                AppCard(
                    variant = CardVariant.Outlined,
                    onClick = { onUserClick(user.id) },
                    contentPadding = 0.dp,
                ) {
                    AppListItem(
                        headline = user.name,
                        supportingText = user.email,
                        leadingContent = {
                            Box(
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = user.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        },
                    )
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
private fun UsersScreenSuccessPreview() {
    MaterialTheme {
        UsersScreen(
            uiState =
                UsersUiState(
                    usersState =
                        AsyncState.Success(
                            listOf(
                                User("1", "Alice Johnson", "alice@example.com"),
                                User("2", "Bob Smith", "bob@example.com"),
                                User("3", "Carol Williams", "carol@example.com"),
                            ),
                        ),
                ),
            onRetry = {},
            onUserClick = {},
        )
    }
}

@Preview(name = "Loading State", showBackground = true)
@Composable
private fun UsersScreenLoadingPreview() {
    MaterialTheme {
        UsersScreen(
            uiState = UsersUiState(usersState = AsyncState.Loading),
            onRetry = {},
            onUserClick = {},
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun UsersScreenErrorPreview() {
    MaterialTheme {
        UsersScreen(
            uiState =
                UsersUiState(
                    usersState = AsyncState.Error("Unable to connect to server. Check your network."),
                ),
            onRetry = {},
            onUserClick = {},
        )
    }
}

@Preview(name = "Empty State", showBackground = true)
@Composable
private fun UsersScreenEmptyPreview() {
    MaterialTheme {
        UsersScreen(
            uiState = UsersUiState(usersState = AsyncState.Success(emptyList())),
            onRetry = {},
            onUserClick = {},
        )
    }
}
