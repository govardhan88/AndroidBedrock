package govi.buildlogic.scaffolding.templates

object PresentationTemplates {
    fun uiState(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.presentation

        import govi.core.ui.AsyncState
        import govi.feature.$featureName.domain.model.${className}Item

        data class ${className}UiState(
            val itemsState: AsyncState<List<${className}Item>> = AsyncState.Loading,
        )
        """.trimIndent() + "\n"

    fun viewModel(
        featureName: String,
        className: String,
    ): String =
        """
        package govi.feature.$featureName.presentation

        import androidx.lifecycle.ViewModel
        import androidx.lifecycle.viewModelScope
        import dagger.hilt.android.lifecycle.HiltViewModel
        import govi.core.navigation.AppNavigator
        import govi.core.ui.AsyncState
        import govi.feature.$featureName.domain.usecase.Get${className}ItemsUseCase
        import kotlinx.coroutines.flow.MutableStateFlow
        import kotlinx.coroutines.flow.StateFlow
        import kotlinx.coroutines.flow.asStateFlow
        import kotlinx.coroutines.flow.update
        import kotlinx.coroutines.launch
        import javax.inject.Inject

        @HiltViewModel
        class ${className}ViewModel
            @Inject
            constructor(
                private val getItemsUseCase: Get${className}ItemsUseCase,
                private val appNavigator: AppNavigator,
            ) : ViewModel() {
                private val _uiState = MutableStateFlow(${className}UiState())
                val uiState: StateFlow<${className}UiState> = _uiState.asStateFlow()

                init {
                    loadItems()
                }

                fun loadItems() {
                    viewModelScope.launch {
                        _uiState.update { it.copy(itemsState = AsyncState.Loading) }
                        getItemsUseCase().fold(
                            onSuccess = { items ->
                                _uiState.update { it.copy(itemsState = AsyncState.Success(items)) }
                            },
                            onFailure = { error ->
                                _uiState.update {
                                    it.copy(
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
        import govi.feature.$featureName.domain.model.${className}Item

        @Composable
        fun ${className}Screen(
            modifier: Modifier = Modifier,
            viewModel: ${className}ViewModel = hiltViewModel(),
        ) {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            Scaffold(
                topBar = {
                    AppTopBar(
                        title = "$className",
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
                    ${className}ListContent(items = items)
                }
            }
        }

        @Composable
        fun ${className}ListContent(
            items: List<${className}Item>,
            modifier: Modifier = Modifier,
        ) {
            if (items.isEmpty()) {
                EmptyStateView(
                    title = "No $className Available",
                    message = "There are currently no items to display.",
                )
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
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
        fun ${className}ScreenPreview() {
            MaterialTheme {
                ${className}ListContent(
                    items =
                        listOf(
                            ${className}Item("1", "Sample Item 1", "Description for item 1"),
                            ${className}Item("2", "Sample Item 2", "Description for item 2"),
                        ),
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
        import govi.feature.$featureName.presentation.${className}Screen
        import javax.inject.Inject

        class ${className}FeatureEntryImpl
            @Inject
            constructor() : FeatureEntry {
                override fun registerRoute(
                    navGraphBuilder: NavGraphBuilder,
                    navController: NavHostController,
                ) {
                    navGraphBuilder.composable<AppRoute.$className> {
                        ${className}Screen()
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
