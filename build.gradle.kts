import java.util.Locale

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}

tasks.register("createFeature") {
    description = "Scaffolds a new Clean Architecture multi-module feature with domain, data, and presentation submodules"
    group = "scaffolding"

    doLast {
        val rawName = project.gradle.startParameter.projectProperties["name"]
            ?: project.providers.gradleProperty("name").orNull?.takeIf { it != project.name }

        if (rawName.isNullOrBlank()) {
            throw GradleException(
                """
                Missing required parameter 'name'.
                Usage: ./gradlew createFeature -Pname=<feature_name>
                Example: ./gradlew createFeature -Pname=products
                """.trimIndent()
            )
        }

        val featureName = rawName.lowercase(Locale.ROOT)
        val className = featureName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }

        val featureRoot = file("feature/$featureName")
        if (featureRoot.exists()) {
            throw GradleException("Feature directory already exists at: ${featureRoot.path}")
        }

        val domainDir = file("feature/$featureName/domain")
        val dataDir = file("feature/$featureName/data")
        val presentationDir = file("feature/$featureName/presentation")

        val domainPackagePath = "govi/feature/$featureName/domain"
        val dataPackagePath = "govi/feature/$featureName/data"
        val presentationPackagePath = "govi/feature/$featureName/presentation"

        val domainSrcDir = file("feature/$featureName/domain/src/main/kotlin/$domainPackagePath")
        val dataSrcDir = file("feature/$featureName/data/src/main/kotlin/$dataPackagePath")
        val presentationSrcDir = file("feature/$featureName/presentation/src/main/kotlin/$presentationPackagePath")

        domainSrcDir.mkdirs()
        dataSrcDir.mkdirs()
        presentationSrcDir.mkdirs()

        // 1. Scaffolding :domain submodule
        file("$domainDir/build.gradle.kts").writeText(
            """
            plugins {
                id("govi.kotlin.jvm")
            }

            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.javax.inject)
            }
            """.trimIndent() + "\n"
        )

        file("$domainSrcDir/model/${className}Item.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                package govi.feature.$featureName.domain.model

                data class ${className}Item(
                    val id: String,
                    val title: String,
                    val description: String = ""
                )
                """.trimIndent() + "\n"
            )
        }

        file("$domainSrcDir/repository/${className}Repository.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                package govi.feature.$featureName.domain.repository

                import govi.feature.$featureName.domain.model.${className}Item

                interface ${className}Repository {
                    suspend fun getItems(): Result<List<${className}Item>>
                    suspend fun getItemById(id: String): Result<${className}Item>
                }
                """.trimIndent() + "\n"
            )
        }

        file("$domainSrcDir/usecase/Get${className}ItemsUseCase.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                package govi.feature.$featureName.domain.usecase

                import govi.feature.$featureName.domain.model.${className}Item
                import govi.feature.$featureName.domain.repository.${className}Repository
                import javax.inject.Inject

                class Get${className}ItemsUseCase @Inject constructor(
                    private val repository: ${className}Repository
                ) {
                    suspend operator fun invoke(): Result<List<${className}Item>> {
                        return repository.getItems()
                    }
                }
                """.trimIndent() + "\n"
            )
        }

        // 2. Scaffolding :data submodule
        file("$dataDir/build.gradle.kts").writeText(
            """
            plugins {
                id("govi.android.library")
                id("govi.android.hilt")
            }

            android {
                namespace = "govi.feature.$featureName.data"
            }

            dependencies {
                implementation(project(":feature:$featureName:domain"))
                implementation(project(":core:network"))

                implementation(libs.retrofit.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.okhttp.logging)
                implementation(libs.kotlinx.coroutines.core)
            }
            """.trimIndent() + "\n"
        )

        file("$dataSrcDir/remote/${className}Dto.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                package govi.feature.$featureName.data.remote

                import govi.feature.$featureName.domain.model.${className}Item
                import kotlinx.serialization.SerialName
                import kotlinx.serialization.Serializable

                @Serializable
                data class ${className}Dto(
                    @SerialName("id") val id: String,
                    @SerialName("title") val title: String,
                    @SerialName("description") val description: String? = null
                ) {
                    fun toDomain(): ${className}Item = ${className}Item(
                        id = id,
                        title = title,
                        description = description.orEmpty()
                    )
                }
                """.trimIndent() + "\n"
            )
        }

        file("$dataSrcDir/remote/${className}ApiService.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                package govi.feature.$featureName.data.remote

                import retrofit2.Response
                import retrofit2.http.GET
                import retrofit2.http.Path

                interface ${className}ApiService {
                    @GET("$featureName")
                    suspend fun getItems(): Response<List<${className}Dto>>

                    @GET("$featureName/{id}")
                    suspend fun getItemById(@Path("id") id: String): Response<${className}Dto>
                }
                """.trimIndent() + "\n"
            )
        }

        file("$dataSrcDir/repository/${className}RepositoryImpl.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                package govi.feature.$featureName.data.repository

                import govi.core.network.NetworkResult
                import govi.core.network.safeApiCall
                import govi.feature.$featureName.data.remote.${className}ApiService
                import govi.feature.$featureName.domain.model.${className}Item
                import govi.feature.$featureName.domain.repository.${className}Repository
                import javax.inject.Inject

                class ${className}RepositoryImpl @Inject constructor(
                    private val apiService: ${className}ApiService
                ) : ${className}Repository {

                    override suspend fun getItems(): Result<List<${className}Item>> {
                        return when (val result = safeApiCall { apiService.getItems() }) {
                            is NetworkResult.Success -> Result.success(result.data.map { it.toDomain() })
                            is NetworkResult.Error -> Result.failure(Exception("HTTP ${"$"}{result.code}: ${"$"}{result.message}"))
                            is NetworkResult.Exception -> Result.failure(result.throwable)
                        }
                    }

                    override suspend fun getItemById(id: String): Result<${className}Item> {
                        return when (val result = safeApiCall { apiService.getItemById(id) }) {
                            is NetworkResult.Success -> Result.success(result.data.toDomain())
                            is NetworkResult.Error -> Result.failure(Exception("HTTP ${"$"}{result.code}: ${"$"}{result.message}"))
                            is NetworkResult.Exception -> Result.failure(result.throwable)
                        }
                    }
                }
                """.trimIndent() + "\n"
            )
        }

        file("$dataSrcDir/di/${className}DataModule.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                package govi.feature.$featureName.data.di

                import dagger.Binds
                import dagger.Module
                import dagger.Provides
                import dagger.hilt.InstallIn
                import dagger.hilt.components.SingletonComponent
                import govi.feature.$featureName.data.remote.${className}ApiService
                import govi.feature.$featureName.data.repository.${className}RepositoryImpl
                import govi.feature.$featureName.domain.repository.${className}Repository
                import retrofit2.Retrofit
                import javax.inject.Singleton

                @Module
                @InstallIn(SingletonComponent::class)
                abstract class ${className}DataModule {

                    @Binds
                    @Singleton
                    abstract fun bind${className}Repository(
                        impl: ${className}RepositoryImpl
                    ): ${className}Repository

                    companion object {
                        @Provides
                        @Singleton
                        fun provide${className}ApiService(retrofit: Retrofit): ${className}ApiService {
                            return retrofit.create(${className}ApiService::class.java)
                        }
                    }
                }
                """.trimIndent() + "\n"
            )
        }

        // 3. Scaffolding :presentation submodule
        file("$presentationDir/build.gradle.kts").writeText(
            """
            plugins {
                id("govi.android.feature")
            }

            android {
                namespace = "govi.feature.$featureName.presentation"
            }

            dependencies {
                implementation(project(":feature:$featureName:domain"))
                implementation(project(":core:navigation"))
            }
            """.trimIndent() + "\n"
        )

        file("$presentationSrcDir/${className}UiState.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                package govi.feature.$featureName.presentation

                import govi.core.ui.AsyncState
                import govi.feature.$featureName.domain.model.${className}Item

                data class ${className}UiState(
                    val itemsState: AsyncState<List<${className}Item>> = AsyncState.Loading
                )
                """.trimIndent() + "\n"
            )
        }

        file("$presentationSrcDir/${className}ViewModel.kt").apply {
            parentFile.mkdirs()
            writeText(
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
                class ${className}ViewModel @Inject constructor(
                    private val getItemsUseCase: Get${className}ItemsUseCase,
                    private val appNavigator: AppNavigator
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
                                            itemsState = AsyncState.Error(
                                                error.message ?: "An unexpected error occurred"
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }

                    fun onBackClicked() {
                        appNavigator.navigateBack()
                    }
                }
                """.trimIndent() + "\n"
            )
        }

        file("$presentationSrcDir/${className}Screen.kt").apply {
            parentFile.mkdirs()
            writeText(
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
                    viewModel: ${className}ViewModel = hiltViewModel()
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    Scaffold(
                        topBar = {
                            AppTopBar(
                                title = "${className}",
                                onBackClick = viewModel::onBackClicked
                            )
                        },
                        modifier = modifier.fillMaxSize()
                    ) { innerPadding ->
                        AsyncContent(
                            state = uiState.itemsState,
                            onRetry = viewModel::loadItems,
                            loadingContent = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    repeat(4) { ShimmerListItem() }
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) { items ->
                            ${className}ListContent(items = items)
                        }
                    }
                }

                @Composable
                fun ${className}ListContent(
                    items: List<${className}Item>,
                    modifier: Modifier = Modifier
                ) {
                    if (items.isEmpty()) {
                        EmptyStateView(
                            title = "No ${className} Available",
                            message = "There are currently no items to display."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items, key = { it.id }) { item ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        if (item.description.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = item.description,
                                                style = MaterialTheme.typography.bodyMedium
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
                            items = listOf(
                                ${className}Item("1", "Sample Item 1", "Description for item 1"),
                                ${className}Item("2", "Sample Item 2", "Description for item 2")
                            )
                        )
                    }
                }
                """.trimIndent() + "\n"
            )
        }

        file("$presentationSrcDir/navigation/${className}FeatureEntryImpl.kt").apply {
            parentFile.mkdirs()
            writeText(
                """
                package govi.feature.$featureName.presentation.navigation

                import androidx.navigation.NavGraphBuilder
                import androidx.navigation.NavHostController
                import androidx.navigation.compose.composable
                import govi.core.navigation.FeatureEntry
                import govi.feature.$featureName.presentation.${className}Screen
                import javax.inject.Inject

                class ${className}FeatureEntryImpl @Inject constructor() : FeatureEntry {
                    override val featureRoute: String = "$featureName"

                    override fun registerRoute(
                        navGraphBuilder: NavGraphBuilder,
                        navController: NavHostController
                    ) {
                        navGraphBuilder.composable(featureRoute) {
                            ${className}Screen()
                        }
                    }
                }
                """.trimIndent() + "\n"
            )
        }

        file("$presentationSrcDir/di/${className}NavigationModule.kt").apply {
            parentFile.mkdirs()
            writeText(
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
                    abstract fun bind${className}FeatureEntry(
                        entry: ${className}FeatureEntryImpl
                    ): FeatureEntry
                }
                """.trimIndent() + "\n"
            )
        }

        // 4. Update settings.gradle.kts
        val settingsFile = file("settings.gradle.kts")
        val settingsContent = settingsFile.readText()
        val modulesToAdd = listOf(
            ":feature:$featureName:domain",
            ":feature:$featureName:data",
            ":feature:$featureName:presentation"
        ).filterNot { settingsContent.contains("\"$it\"") }

        if (modulesToAdd.isNotEmpty()) {
            val appendIncludes = modulesToAdd.joinToString("\n") { "include(\"$it\")" }
            settingsFile.appendText("\n// Feature: $featureName\n$appendIncludes\n")
            println("✓ Added modules to settings.gradle.kts: $modulesToAdd")
        }

        // 5. Inject dependencies into app/build.gradle.kts
        val appBuildFile = file("app/build.gradle.kts")
        if (appBuildFile.exists()) {
            var appContent = appBuildFile.readText()
            val presDep = "implementation(project(\":feature:$featureName:presentation\"))"
            val dataDep = "implementation(project(\":feature:$featureName:data\"))"

            val dependenciesIndex = appContent.indexOf("dependencies {")
            if (dependenciesIndex != -1) {
                val insertIndex = dependenciesIndex + "dependencies {".length
                val depsToInsert = buildString {
                    append("\n    // Feature $featureName")
                    if (!appContent.contains(presDep)) {
                        append("\n    $presDep")
                    }
                    if (!appContent.contains(dataDep)) {
                        append("\n    $dataDep")
                    }
                }
                appContent = appContent.substring(0, insertIndex) + depsToInsert + appContent.substring(insertIndex)
                appBuildFile.writeText(appContent)
                println("✓ Injected :presentation and :data dependencies into app/build.gradle.kts")
            }
        }

        println("✓ Successfully created feature '$featureName' with Clean Architecture (:domain, :data, :presentation)!")
    }
}
