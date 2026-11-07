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
│   ├── network/                        # Retrofit, OkHttpClient, logging interceptors, NetworkResult
│   └── ui/                             # Generic AsyncState<T> and reusable AsyncContent composable
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

## 🌐 Environment & CI/CD Dynamic Configuration

Base URLs and API environments are resolved dynamically using a four-tier fallback hierarchy:

$$\text{Gradle Flag (-P)} \;\rightarrow\; \text{Environment / CI Secrets} \;\rightarrow\; \text{local.properties} \;\rightarrow\; \text{Default Fallback}$$

### 1. Local Development (`local.properties` - Gitignored)
Override base URLs on your local machine without committing secrets:
```properties
DEV_BASE_URL=https://api.dev.example.com/
PROD_BASE_URL=https://api.example.com/
```

### 2. GitHub Actions CI/CD (`.github/workflows/ci.yml`)
Store endpoints under **GitHub Repo Settings > Secrets and variables > Actions**:
* `DEV_BASE_URL`
* `PROD_BASE_URL`

Injected automatically into the build via `ORG_GRADLE_PROJECT_` prefix or CLI flags.

## 🌐 2. Network & Authentication Architecture (`:core:network`)

### Optimized Network Logging (`NetworkLoggingConfig`)
* **Environment-Aware**: Logging is dynamically configured via `NetworkLoggingConfig` (driven by `BuildConfig.DEBUG`).
* **Zero Production Overhead**: In release builds, the logging interceptor is **completely omitted** from the `OkHttpClient` pipeline, avoiding interceptor chain overhead.
* **Sensitive Header Redaction**: Critical authentication headers (`Authorization`, `Cookie`, `Set-Cookie`, `X-Api-Key`, `Proxy-Authorization`, `Token`) are automatically redacted with `██` in logs.
* **4KB Logcat Chunking**: OkHttp output is chunked into 3,500-character segments to prevent Android Logcat's hard line-truncation limit.

### Pluggable Token Authentication (`AuthTokenProvider` & `TokenAuthenticator`)
* **`AuthTokenProvider`**: Optional contract for providing cached access tokens and executing token refreshes.
* **`AuthInterceptor`**: Automatically attaches `Authorization: Bearer <token>` to outgoing requests. Public endpoints can bypass authentication using `@Headers("${AuthInterceptor.HEADER_NO_AUTH}: true")`.
* **`TokenAuthenticator`**: OkHttp `Authenticator` handling HTTP 401 challenges with thread-safe mutex synchronization to prevent concurrent token refresh stampedes.
* **Zero-Configuration Pluggability**: Bound via Dagger `@BindsOptionalOf`. If an application does not bind an `AuthTokenProvider`, OkHttp operates with zero auth overhead.
* **Multibound Interceptors (`@NetworkInterceptors`)**: Any module can contribute custom OkHttp interceptors (e.g. Chucker, Cache, Analytics) via Dagger Hilt `@Provides @IntoSet @NetworkInterceptors`.

---

## 🧭 3. Navigation & Multibindings Architecture

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

## 🎨 4. Bedrock UI & Design System (`:core:ui`)

`:core:ui` provides a production-grade, enterprise-ready Design System for all Jetpack Compose feature presentation modules:

### Design Tokens & Theming (`govi.core.ui.theme`)
* **`AppTheme`**: Material 3 theme supporting dynamic color on Android 12+ (`dynamicColor = true`), dark/light theme switching, and accessible color roles.
* **Tokens**: Centralized color palette, typography scale (`AppTypography`), shape scales (`AppShapes`), and spacing tokens (`Spacing`, `MaterialTheme.spacing.medium`).

### Form & Input Controls (`govi.core.ui.component.input`)
* **`AppTextField`**: Standard text input supporting labels, placeholders, leading icons, clear button, and inline validation error messages.
* **`AppPasswordTextField`**: Secure password field with visibility toggle eye icon and password masking.
* **`AppSearchField`**: Search input field with search icon, clear button, and `ImeAction.Search` callback.

### Dialogs & Bottom Sheets (`govi.core.ui.component.dialog`)
* **`AppConfirmationDialog`**: Alert dialog for confirmations, dismissals, or destructive delete actions (`isDestructive = true`).
* **`AppModalBottomSheet`**: Modal bottom sheet container with drag handle, title, close action, and content slot.

### Status Badges & Chips (`govi.core.ui.component.badge`)
* **`StatusBadge`**: Visual status tag for entity states (e.g. `Success`, `Warning`, `Error`, `Info`, `Neutral`) with background tints and dot indicators.

### Cards, Lists & Dividers (`govi.core.ui.component.card`)
* **`AppCard`**: Standardized cards supporting `Elevated`, `Outlined`, and `Filled` variants with unified padding.
* **`AppListItem`**: Standard list item row with leading icon/avatar, title, subtitle, and trailing chevron/action slot.
* **`AppDivider`**: Standard horizontal separator using theme outlineVariant tokens.

### Asynchronous State & Feedback (`govi.core.ui.component`)
* **`AsyncState<T>`**: Generic state holder (`Loading`, `Success<T>`, `Error`, `Idle`).
* **`AsyncContent`**: Composable with smooth crossfading, default or custom loading spinners, and error views with retry.
* **`FullScreenLoadingView` & `InlineLoadingIndicator`**: Centered full-screen or compact inline spinners with optional status messages.
* **`ErrorView`**: Alert layout with error icon, title, description, and "Retry" CTA.
* **`EmptyStateView`**: Clean zero-data illustration/icon with title, description, and action button.

### Skeleton Shimmer Loading (`govi.core.ui.component.shimmer`)
* **`Modifier.shimmer()`**: Custom animated gradient shimmer effect.
* **`ShimmerBox`**: Rounded shimmer placeholder box.
* **`ShimmerListItem`**: Skeleton card placeholder mimicking list items during initial loading.

### Reusable Buttons & Navigation Bars
* **`PrimaryButton`, `SecondaryButton`, `AppOutlinedButton`**: Material 3 buttons with built-in loading spinners (`isLoading: Boolean`).
* **`AppTextButton` & `AppIconButton`**: Low-emphasis text buttons and icon buttons.
* **`AppTopBar`**: Standardized top bar encapsulating Material 3 experimental APIs with back navigation and action slots.
* **`ObserveAsEvents`**: Lifecycle-aware (`Lifecycle.State.STARTED`) `Flow<T>` observer preventing dropped or duplicate one-time events (Snackbars, toasts, navigation intents).
* **`SnackbarController` & `AppSnackbarHost`**: Decoupled ViewModel-to-UI Snackbar dispatch channel.

### Live Design System Showcase
* **[`DesignSystemShowcase`](file:///Users/tejendranathreddy/AndroidStudioProjects/clean-architecture-template/core/ui/src/main/kotlin/govi/core/ui/preview/DesignSystemShowcase.kt)**: Complete interactive catalog preview displaying all components side-by-side.

---

## ⚡ 5. Automated Feature Scaffolding Task

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
   - `OrdersDto.kt` (kotlinx.serialization `@Serializable` DTO with `.toDomain()` mapper)
   - `OrdersApiService.kt` (Retrofit service)
   - `OrdersRepositoryImpl.kt` (repository implementation using `safeApiCall`)
   - `OrdersDataModule.kt` (Hilt dependency injection module)
   - `build.gradle.kts` with `govi.android.library` and `govi.android.hilt`
3. **Generates `:presentation` submodule**:
   - `OrdersUiState.kt` (State holder leveraging `AsyncState<List<OrdersItem>>`)
   - `OrdersViewModel.kt` (@HiltViewModel with state flow & lifecycle management)
   - `OrdersScreen.kt` (Material3 Composable UI utilizing `AsyncContent`, LazyColumn, and `@Preview`)
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
/Users/tejendranathreddy/AndroidStudioProjects/clean-architecture-template
```
