package govi.buildlogic.scaffolding.templates

object PresentationTemplates {
    fun uiState(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.presentation

        import govi.core.base.mvi.UiEffect
        import govi.core.base.mvi.UiIntent
        import govi.core.base.mvi.UiState
        import govi.core.ui.AsyncState
        import govi.feature.$featureName.domain.model.${className}Item

        data class ${className}UiState(
            val itemsState: AsyncState<List<${className}Item>> = AsyncState.Loading,
        ) : UiState

        sealed interface ${className}Intent : UiIntent {
            data object LoadItems : ${className}Intent
            data object BackClicked : ${className}Intent
        }

        sealed interface ${className}Effect : UiEffect {
            data object NavigateBack : ${className}Effect
        }
        """.trimIndent() + "\n"

    fun viewModel(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.presentation

        import androidx.lifecycle.viewModelScope
        import dagger.hilt.android.lifecycle.HiltViewModel
        import govi.core.base.viewmodel.BaseViewModel
        import govi.core.navigation.AppNavigator
        import govi.core.ui.AsyncState
        import govi.feature.$featureName.domain.usecase.Get${className}ItemsUseCase
        import kotlinx.coroutines.launch
        import javax.inject.Inject

        @HiltViewModel
        class ${className}ViewModel @Inject constructor(
            private val getItemsUseCase: Get${className}ItemsUseCase,
            private val appNavigator: AppNavigator,
        ) : BaseViewModel<${className}UiState, ${className}Intent, ${className}Effect>(${className}UiState()) {
            init {
                loadItems()
            }

            override fun processIntent(intent: ${className}Intent) {
                when (intent) {
                    is ${className}Intent.LoadItems -> loadItems()
                    is ${className}Intent.BackClicked -> onBackClicked()
                }
            }

            fun loadItems() {
                viewModelScope.launch {
                    updateState { copy(itemsState = AsyncState.Loading) }
                    getItemsUseCase().fold(
                        onSuccess = { items ->
                            updateState { copy(itemsState = AsyncState.Success(items)) }
                        },
                        onFailure = { error ->
                            updateState {
                                copy(
                                    itemsState =
                                        AsyncState.Error(
                                            error.message ?: "An unexpected error occurred",
                                        ),
                                )
                            }
                        },
                    )
                }
            }

            fun onBackClicked() {
                appNavigator.navigateBack()
            }
        }
        """.trimIndent() + "\n"

    fun screen(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.presentation

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
        import govi.feature.$featureName.domain.model.${className}Item

        /**
         * Stateful entry point for the $className screen.
         *
         * Connects the [${className}ViewModel] to the stateless [${className}Screen] composable.
         */
        @Composable
        fun ${className}Route(
            modifier: Modifier = Modifier,
            viewModel: ${className}ViewModel = hiltViewModel(),
        ) {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ${className}Screen(
                uiState = uiState,
                onRetry = viewModel::loadItems,
                onBackClick = viewModel::onBackClicked,
                modifier = modifier,
            )
        }

        /**
         * Stateless $className screen rendering UI states (Loading, Success, Error, Empty).
         *
         * Free from Android/Hilt dependencies for instant Compose Previews and deterministic UI testing.
         */
        @Composable
        fun ${className}Screen(
            uiState: ${className}UiState,
            onRetry: () -> Unit,
            onBackClick: () -> Unit,
            modifier: Modifier = Modifier,
        ) {
            val spacing = MaterialTheme.spacing

            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "$className",
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
                    ${className}ListContent(items = items)
                }
            }
        }

        @Composable
        fun ${className}ListContent(
            items: List<${className}Item>,
            modifier: Modifier = Modifier,
        ) {
            val spacing = MaterialTheme.spacing

            if (items.isEmpty()) {
                EmptyStateView(
                    title = "No $className Available",
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
        private fun ${className}ScreenSuccessPreview() {
            MaterialTheme {
                ${className}Screen(
                    uiState =
                        ${className}UiState(
                            itemsState =
                                AsyncState.Success(
                                    listOf(
                                        ${className}Item("1", "Sample Item 1", "Description for item 1"),
                                        ${className}Item("2", "Sample Item 2", "Description for item 2"),
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
        private fun ${className}ScreenLoadingPreview() {
            MaterialTheme {
                ${className}Screen(
                    uiState = ${className}UiState(itemsState = AsyncState.Loading),
                    onRetry = {},
                    onBackClick = {},
                )
            }
        }

        @Preview(name = "Error State", showBackground = true)
        @Composable
        private fun ${className}ScreenErrorPreview() {
            MaterialTheme {
                ${className}Screen(
                    uiState =
                        ${className}UiState(
                            itemsState = AsyncState.Error("Failed to fetch $featureName details."),
                        ),
                    onRetry = {},
                    onBackClick = {},
                )
            }
        }

        @Preview(name = "Empty State", showBackground = true)
        @Composable
        private fun ${className}ScreenEmptyPreview() {
            MaterialTheme {
                ${className}Screen(
                    uiState = ${className}UiState(itemsState = AsyncState.Success(emptyList())),
                    onRetry = {},
                    onBackClick = {},
                )
            }
        }
        """.trimIndent() + "\n"

    fun featureEntry(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.presentation.navigation

        import androidx.navigation.NavGraphBuilder
        import androidx.navigation.NavHostController
        import androidx.navigation.compose.composable
        import govi.core.navigation.FeatureEntry
        import govi.core.navigation.route.AppRoute
        import govi.feature.$featureName.presentation.${className}Route
        import javax.inject.Inject

        class ${className}FeatureEntryImpl @Inject constructor() : FeatureEntry {
            override fun registerRoute(
                navGraphBuilder: NavGraphBuilder,
                navController: NavHostController,
            ) {
                navGraphBuilder.composable<AppRoute.$className> {
                    ${className}Route()
                }
            }
        }
        """.trimIndent() + "\n"

    fun navigationModule(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.presentation.di

        import dagger.Binds
        import dagger.Module
        import dagger.hilt.InstallIn
        import dagger.hilt.components.SingletonComponent
        import dagger.multibindings.IntoSet
        import govi.core.navigation.FeatureEntry
        import govi.feature.$featureName.presentation.navigation.${className}FeatureEntryImpl

        @Module
        @InstallIn(SingletonComponent::class)
        abstract class ${className}NavigationModule {
            @Binds
            @IntoSet
            abstract fun bind${className}FeatureEntry(entry: ${className}FeatureEntryImpl): FeatureEntry
        }
        """.trimIndent() + "\n"
}
