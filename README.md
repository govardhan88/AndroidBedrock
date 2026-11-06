# Govi Clean Architecture Android Starter Template

A modular, production-grade Android Clean Architecture starter template powered by Gradle Convention Plugins, Jetpack Compose, Kotlin 2.x, Dagger Hilt, Retrofit, and automated feature scaffolding.

---

## 🏛 Multi-Module Topology

The repository follows Google's recommended multi-module Clean Architecture guidelines, strictly enforcing unidirectional dependencies and layer isolation:

```
├── app/                                # Application entry point (Hilt root, Navigation host)
├── build-logic/                        # Composite Gradle build for convention plugins
│   └── convention/                     # Reusable Gradle plugins
├── core/
│   ├── navigation/                     # AppNavigator, Navigation Channel, FeatureEntry multibinding
│   └── network/                        # Retrofit, OkHttpClient, logging interceptors, NetworkResult
└── feature/
    ├── users/                          # Reference Feature
    │   ├── domain/                     # Pure Kotlin (govi.kotlin.jvm): Models, UseCases, Repositories
    │   ├── data/                       # Android Library (govi.android.library, hilt): DTOs, API, Impl
    │   └── presentation/               # Android Feature (govi.android.feature): Compose UI, ViewModel, FeatureEntry
    └── <scaffolded_features>/...       # Features generated via ./gradlew createFeature -Pname=<feature>
```

---

## 🛠 1. Gradle Convention Plugins (`build-logic`)

All build logic is centralized in the `build-logic` composite build using Gradle Version Catalogs (`gradle/libs.versions.toml`).

| Plugin ID | Class Name | Purpose |
|---|---|---|
| `govi.android.library` | `AndroidLibraryConventionPlugin` | Configures Android library target SDK, min SDK, Java 21, coroutines, and testing |
| `govi.android.compose` | `AndroidComposeConventionPlugin` | Enables Compose, Compose BOM, Material3, and **Compose Compiler metrics/reports** |
| `govi.android.hilt` | `AndroidHiltConventionPlugin` | Configures Dagger Hilt with KSP annotation processing |
| `govi.android.feature` | `AndroidFeatureConventionPlugin` | Aggregates `library`, `compose`, `hilt`, and `:core:navigation` dependencies |
| `govi.kotlin.jvm` | `KotlinJvmConventionPlugin` | Pure Kotlin/JVM module setup for clean domain modules (zero Android dependencies) |

### Compose Compiler Metrics & Reports

The `govi.android.compose` plugin supports Compose compiler stability metrics and reports. To activate:

```bash
./gradlew compileDebugKotlin -PcomposeCompilerMetrics=true -PcomposeCompilerReports=true
```

Reports and metrics are written to:
- `<module>/build/compose-reports/` (`*-classes.txt`, `*-composables.txt`, `*-composables.csv`)
- `<module>/build/compose-metrics/`

---

## 🧭 2. Navigation & Multibindings Architecture

### `AppNavigator` Channel
Navigation events are dispatched via an asynchronous Kotlin `Channel` / `Flow` decoupled from Android UI classes:

```kotlin
interface AppNavigator {
    val navigationChannel: Flow<NavigationIntent>
    fun navigateTo(route: String, popUpToRoute: String? = null, inclusive: Boolean = false, isSingleTop: Boolean = true)
    fun navigateBack()
    fun popTo(route: String, inclusive: Boolean = false)
}
```

### Multibound `FeatureEntry`
Feature presentation modules register their Jetpack Compose routes into Dagger Hilt's multibinding Set:

```kotlin
interface FeatureEntry {
    val featureRoute: String
    fun registerRoute(navGraphBuilder: NavGraphBuilder, navController: NavHostController)
}
```

In `:app`, `MainActivity` injects `Set<FeatureEntry>` and observes `AppNavigator`:

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var appNavigator: AppNavigator
    @Inject lateinit var featureEntries: Set<@JvmSuppressWildcards FeatureEntry>
    ...
}
```

---

## ⚡ 3. Automated Feature Scaffolding Task

To scaffold a complete Clean Architecture feature module hierarchy in seconds, execute:

```bash
./gradlew createFeature -Pname=<feature_name>
```

### Example:
```bash
./gradlew createFeature -Pname=orders
```

### Automated Actions Executed:
1. **Generates `:domain` submodule**:
   - `OrdersItem.kt` (domain model)
   - `OrdersRepository.kt` (repository interface)
   - `GetOrdersItemsUseCase.kt` (domain use case)
   - `build.gradle.kts` with `govi.kotlin.jvm`
2. **Generates `:data` submodule**:
   - `OrdersDto.kt` (data transfer object with `.toDomain()` mapper)
   - `OrdersApiService.kt` (Retrofit service)
   - `OrdersRepositoryImpl.kt` (repository implementation using `safeApiCall`)
   - `OrdersDataModule.kt` (Hilt dependency injection module)
   - `build.gradle.kts` with `govi.android.library` and `govi.android.hilt`
3. **Generates `:presentation` submodule**:
   - `OrdersUiState.kt` (Sealed interface with Loading, Success, Error states)
   - `OrdersViewModel.kt` (@HiltViewModel with state flow & lifecycle management)
   - `OrdersScreen.kt` (Material3 Composable UI with LazyColumn and `@Preview`)
   - `OrdersFeatureEntryImpl.kt` (`FeatureEntry` multibinding implementation)
   - `OrdersNavigationModule.kt` (Hilt `@IntoSet` multibinding module)
   - `build.gradle.kts` with `govi.android.feature`
4. **Updates `settings.gradle.kts`**:
   - Appends `include(":feature:<name>:domain")`, `include(":feature:<name>:data")`, `include(":feature:<name>:presentation")`
5. **Updates `app/build.gradle.kts`**:
   - Automatically injects `:presentation` and `:data` dependencies into the `dependencies { ... }` block.

---

## 🚀 Building & Running

### Build All Modules
```bash
./gradlew assembleDebug
```

### Run Unit Tests
```bash
./gradlew test
```

### Setting Active Workspace in Antigravity IDE
Open the folder in Antigravity or VS Code / Android Studio:
```
/Users/tejendranathreddy/.gemini/antigravity/scratch/clean-architecture-template
```
