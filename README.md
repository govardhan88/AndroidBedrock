# Android Bedrock - Clean Architecture Starter Template

A modular, production-grade Android Clean Architecture starter template powered by Gradle Convention Plugins, Jetpack Compose, Kotlin 2.x, Dagger Hilt, Retrofit, and automated feature scaffolding.

---

## 🏛 Multi-Module Topology

The repository follows Google's recommended multi-module Clean Architecture guidelines, strictly enforcing unidirectional dependencies and layer isolation while supporting both **divided** (3 submodules) and **compact** (single module) features within the same application:

```
├── app/                                # Application entry point (Hilt root, Navigation host)
├── build-logic/                        # Composite Gradle build for convention plugins
│   └── convention/                     # Reusable Gradle plugins
├── core/
│   ├── base/                           # Dispatchers (AppDispatchers), MVI BaseViewModel, AppResult, UseCases
│   ├── navigation/                     # AppRoute, AppNavigator, FeatureEntry multibindings
│   ├── network/                        # Retrofit, NetworkMonitor, dynamic logging, auth interceptors
│   ├── datastore/                      # Jetpack DataStore, TokenDataStore, persistent auth tokens
│   └── ui/                             # Bedrock Compose Design System, NoInternetHost, tokens, AsyncState<T>
└── feature/
    │   # 1. Divided Architecture (3 Submodules) - For Complex Core Domains
    ├── users/                          # Reference Divided Feature
    │   ├── domain/                     # Pure Kotlin (bedrock.kotlin.jvm): Models, UseCases, Repositories
    │   ├── data/                       # Android Library (bedrock.android.library, hilt): DTOs, API, Impl
    │   └── presentation/               # Android Feature (bedrock.android.feature): Compose UI, ViewModel, FeatureEntry
    │
    │   # 2. Compact Architecture (Single Module) - For Lightweight / UI-centric Screens
    └── settings/                       # Reference Compact Feature (bedrock.android.feature)
        └── src/main/kotlin/govi/feature/settings/
            ├── domain/                 # Internal UseCase and Models
            ├── data/                   # Internal API service and Repository implementation
            └── presentation/           # Compose Screen, ViewModel, and FeatureEntry multibinding
```

---

## 🛠 1. Gradle Convention Plugins (`build-logic`)

All build logic is centralized in the `build-logic` composite build using Gradle Version Catalogs (`gradle/libs.versions.toml`).

| Plugin ID | Class Name | Purpose |
|---|---|---|
| `bedrock.android.library` | `AndroidLibraryConventionPlugin` | Configures Android library target SDK, min SDK, Java 21, coroutines, and testing |
| `bedrock.android.compose` | `AndroidComposeConventionPlugin` | Enables Compose, Compose BOM, Material3, and **Compose Compiler metrics/reports** |
| `bedrock.android.hilt` | `AndroidHiltConventionPlugin` | Configures Dagger Hilt with KSP annotation processing |
| `bedrock.android.feature` | `AndroidFeatureConventionPlugin` | Aggregates `library`, `compose`, `hilt`, and `:core:navigation` dependencies |
| `bedrock.kotlin.jvm` | `KotlinJvmConventionPlugin` | Pure Kotlin/JVM module setup for clean domain modules (zero Android dependencies) |
| `bedrock.scaffolding` | `ScaffoldingPlugin` | Modular scaffolding system in `build-logic` powering `createFeature` and code generation |

### Compose Compiler Metrics & Reports

The `bedrock.android.compose` plugin supports Compose compiler stability metrics and reports. To activate:

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
* **Loop Breaker**: Rejects infinite 401 cascades when retry count exceeds 3 (`responseCount(response) >= 3`) and dispatches `onAuthFailed()`.
* **Zero-Configuration Pluggability**: Bound via Dagger `@BindsOptionalOf`. If an application does not bind an `AuthTokenProvider`, OkHttp operates with zero auth overhead.
* **Multibound Interceptors (`@NetworkInterceptors`)**: Any module can contribute custom OkHttp interceptors (e.g. Chucker, Cache, Analytics) via Dagger Hilt `@Provides @IntoSet @NetworkInterceptors`.

---

## 💾 2.5. Token Persistence & Jetpack DataStore (`:core:datastore`)

`:core:datastore` provides non-blocking, transaction-safe token and preference storage powered by Android Jetpack DataStore:

* **`TokenDataStore`**: Interface exposing reactive `Flow<String?>` streams and suspend functions for access/refresh tokens.
* **`TokenDataStoreImpl`**: Implementation backed by `DataStore<Preferences>`.
* **`DefaultAuthTokenProvider`**: Production-ready base class connecting `TokenDataStore` directly into `:core:network`'s `AuthTokenProvider` contract.
* **`DataStoreModule`**: Hilt module injecting singleton `DataStore<Preferences>` via `PreferenceDataStoreFactory`.

---

## 🧱 2.6. Core Base Module (`:core:base`)

`:core:base` provides the foundational architecture abstractions, concurrency management, and MVI foundation for all features and core modules:

### 1. Concurrency & Dispatchers (`com.govi.androidbedrock.core.base.dispatcher`)
* **`AppDispatchers`**: Centralized contract exposing `io`, `default`, `main`, `mainImmediate`, and `unconfined` coroutine dispatchers for deterministic testing.
* **`DefaultAppDispatchers`**: Production implementation backed by standard `kotlinx.coroutines.Dispatchers`.
* **Qualifiers**: `@IoDispatcher`, `@DefaultDispatcher`, `@MainDispatcher`, `@MainImmediateDispatcher` for dependency injection.
* **`DispatchersModule`**: Hilt module providing dispatcher bindings across the app.

### 2. MVI & Base ViewModel (`com.govi.androidbedrock.core.base.mvi` & `com.govi.androidbedrock.core.base.viewmodel`)
* **`UiState`**: Marker interface for immutable screen state objects.
* **`UiIntent`**: Marker interface for user actions and intents.
* **`UiEffect`**: Marker interface for one-time side effects (navigation, toasts, snackbars).
* **`BaseViewModel<State, Intent, Effect>`**:
  * Exposes thread-safe `StateFlow<State>`.
  * Emits single-shot events via buffered `Channel<Effect>` exposed as `Flow<Effect>`.
  * Encapsulates `updateState { ... }` and `sendEffect(...)`.
  * Abstract `processIntent(intent: Intent)` enforcing Unidirectional Data Flow (UDF).

### 3. Functional Result & Error Monad (`com.govi.androidbedrock.core.base.result`)
* **`AppResult<T>`**: Strongly-typed result container (`Success<T>`, `Error`, `Loading`) with functional operators:
  * `map { ... }`: Value transformation preserving error and loading states.
  * `onSuccess { ... }` & `onError { ... }`: Declarative chaining.
  * `getOrNull()`: Safe nullable extraction.
* **`AppError`**: Structured domain error hierarchy:
  * `Network`, `Server`, `Authentication`, `NotFound`, `Validation`, `Unknown`.

### 4. Base Use Cases (`com.govi.androidbedrock.core.base.usecase`)
* **`BaseUseCase<P, R>`**: Suspending use case executing on `AppDispatchers.io` with automated exception wrapping into `AppResult.Error(AppError.Unknown)`.
* **`FlowUseCase<P, R>`**: Reactive use case emitting streams on `AppDispatchers.io` with structured `.catch { ... }` error handling.

---

## 🧭 3. Type-Safe Navigation & Multibindings Architecture (`:core:navigation`)

The template completely eliminates fragile raw string literals (e.g. `"products/123"`) by utilizing **Jetpack Compose Type-Safe Navigation** powered by `kotlinx.serialization`:

### Type-Safe Route Contracts (`AppRoute`)
All screen destinations and their typed arguments are declared centrally in `:core:navigation`:

```kotlin
@Serializable
sealed interface AppRoute {
    @Serializable
    data object Users : AppRoute

    @Serializable
    data object Products : AppRoute

    @Serializable
    data class ProductDetail(val productId: String) : AppRoute

    @Serializable
    data object Settings : AppRoute
}
```

### `AppNavigator` Channel
Navigation events are dispatched via an asynchronous Kotlin `Channel` / `Flow` decoupled from Android UI classes:

```kotlin
interface AppNavigator {
    val navigationChannel: Flow<NavigationIntent>

    // 100% type-safe compile-time navigation with autocomplete
    fun navigateTo(route: Any, popUpToRoute: Any? = null, inclusive: Boolean = false, isSingleTop: Boolean = true)
    fun navigateBack()
    fun popTo(route: Any, inclusive: Boolean = false)
}
```

### Multibound `FeatureEntry`
Feature presentation modules register their Jetpack Compose routes using the typed route contract:

```kotlin
class UsersFeatureEntryImpl @Inject constructor() : FeatureEntry {
    override fun registerRoute(navGraphBuilder: NavGraphBuilder, navController: NavHostController) {
        navGraphBuilder.composable<AppRoute.Users> {
            UsersScreen()
        }
    }
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

### Design Tokens & Theming (`com.govi.androidbedrock.core.ui.theme`)
* **`AppTheme`**: Material 3 theme supporting dynamic color on Android 12+ (`dynamicColor = true`), dark/light theme switching, and accessible color roles.
* **Tokens**: Centralized color palette, typography scale (`AppTypography`), shape scales (`AppShapes`), and spacing tokens (`Spacing`, `MaterialTheme.spacing.medium`).

### Form & Input Controls (`com.govi.androidbedrock.core.ui.component.input`)
* **`AppTextField`**: Standard text input supporting labels, placeholders, leading icons, clear button, and inline validation error messages.
* **`AppPasswordTextField`**: Secure password field with visibility toggle eye icon and password masking.
* **`AppSearchField`**: Search input field with search icon, clear button, and `ImeAction.Search` callback.

### Dialogs & Bottom Sheets (`com.govi.androidbedrock.core.ui.component.dialog`)
* **`AppConfirmationDialog`**: Alert dialog for confirmations, dismissals, or destructive delete actions (`isDestructive = true`).
* **`AppModalBottomSheet`**: Modal bottom sheet container with drag handle, title, close action, and content slot.

### Status Badges & Chips (`com.govi.androidbedrock.core.ui.component.badge`)
* **`StatusBadge`**: Visual status tag for entity states (e.g. `Success`, `Warning`, `Error`, `Info`, `Neutral`) with background tints and dot indicators.

### Cards, Lists & Dividers (`com.govi.androidbedrock.core.ui.component.card`)
* **`AppCard`**: Standardized cards supporting `Elevated`, `Outlined`, and `Filled` variants with unified padding.
* **`AppListItem`**: Standard list item row with leading icon/avatar, title, subtitle, and trailing chevron/action slot.
* **`AppDivider`**: Standard horizontal separator using theme outlineVariant tokens.

### Asynchronous State & Feedback (`com.govi.androidbedrock.core.ui.component`)
* **`AsyncState<T>`**: Generic state holder (`Loading`, `Success<T>`, `Error`, `Idle`).
* **`AsyncContent`**: Composable with smooth crossfading, default or custom loading spinners, and error views with retry.
* **`FullScreenLoadingView` & `InlineLoadingIndicator`**: Centered full-screen or compact inline spinners with optional status messages.
* **`ErrorView`**: Alert layout with error icon, title, description, and "Retry" CTA.
* **`EmptyStateView`**: Clean zero-data illustration/icon with title, description, and action button.

### Skeleton Shimmer Loading (`com.govi.androidbedrock.core.ui.component.shimmer`)
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

### 🏛️ Jetpack Compose Architecture Principles
All presentation screens adhere to official Android Architecture and Jetpack Compose gold standards:

1. **Stateful Route vs Stateless Screen Pattern**:
   * **`*Route`**: Injects ViewModel via `hiltViewModel()`, collects UI state using `collectAsStateWithLifecycle()`, and binds user actions to ViewModel functions. This is registered in `FeatureEntry`.
   * **`*Screen`**: Pure stateless composable receiving plain state objects (`*UiState`) and lambda callbacks (`onRetry: () -> Unit`, `onBackClick: () -> Unit`). Zero Android/Hilt dependencies for instant Compose Previews and headless UI testing.
   ```kotlin
   // Stateful Route (Injected & Connected)
   @Composable
   fun UsersRoute(
       modifier: Modifier = Modifier,
       viewModel: UsersViewModel = hiltViewModel(),
   ) {
       val uiState by viewModel.uiState.collectAsStateWithLifecycle()
       UsersScreen(
           uiState = uiState,
           onRetry = viewModel::loadUsers,
           onBackClick = viewModel::onBackClicked,
           modifier = modifier,
       )
   }

   // Stateless Screen (Previewable & Pure)
   @Composable
   fun UsersScreen(
       uiState: UsersUiState,
       onRetry: () -> Unit,
       onBackClick: () -> Unit,
       modifier: Modifier = Modifier,
   ) {
       Scaffold(...) { innerPadding ->
           AsyncContent(state = uiState.usersState, onRetry = onRetry) { users ->
               UsersListContent(users = users)
           }
       }
   }
   ```
2. **Design System & Spacing Token Enforcement**:
   * Layouts consistently use `MaterialTheme.spacing` (`spacing.small`, `spacing.medium`, `spacing.large`) rather than ad-hoc dp literals.
   * Visual elements reuse design system primitives like `AppCard`, `AppListItem`, `AppTopBar`, and `EmptyStateView`.
3. **Multi-State Compose Previews**:
   * Every screen provides 4 distinct `@Preview` composables rendered in Android Studio without emulator/backend overhead:
     * `Success State`
     * `Loading State`
     * `Error State`
     * `Empty State`
4. **Scaffolding Automation**:
   * Any new feature generated via `./gradlew createFeature` automatically adheres to this pattern out-of-the-box.

### 🌐 No Internet Architecture & Flexible Presentation Formats (`:core:network` + `:core:ui`)
The template features a comprehensive offline and network monitoring system with pluggable, beautiful UI presentations:

#### 1. Real-Time Network Monitoring (`:core:network`)
* **`NetworkMonitor`**: Clean interface emitting `isOnline: Flow<Boolean>` and providing synchronous `isCurrentlyOnline: Boolean`.
* **`ConnectivityManagerNetworkMonitor`**: Production implementation observing active network capabilities (`NET_CAPABILITY_INTERNET` + `NET_CAPABILITY_VALIDATED`) via Android's `ConnectivityManager`. Bound in `NetworkConnectivityModule`.

#### 2. Design Formats Choice (`NoInternetFormat` - Default: `Dialog`)
Developers can choose between 5 distinct, enterprise-grade design formats:
* **`Dialog` (DEFAULT)**: Polished Material 3 modal alert dialog with an elevated `errorContainer` badge containing `Icons.Default.WifiOff`, supportive explanation, "Try Again" primary button, and "Network Settings" secondary button.
* **`FullScreen`**: Complete page replacement layout with a large 88.dp circular badge, headline, supportive text, and stacked/inline action buttons. Ideal for blocking screens that require active internet.
* **`BottomSheet`**: Slide-up modal bottom sheet with drag handle, centered iconography, and full-width actions.
* **`Banner`**: Non-intrusive animated sticky top banner with `expandVertically() + fadeIn()` transitions. Allows users to continue browsing cached/offline content undisturbed.
* **`Snackbar`**: Floating bottom toast/pill with retry CTA.

#### 3. Automatic System Settings Integration
* **`openWirelessSettings(context)`**: Failsafe intent launcher automatically opening the device's system Wi-Fi / Wireless Settings screen with zero boilerplate.

#### 4. Declarative Usage (`NoInternetHost`)
Wrap any screen or root navigation graph in `NoInternetHost`:
```kotlin
NoInternetHost(
    isOffline = !isOnline,
    format = NoInternetFormat.Dialog, // Default as Dialog; choice to switch to FullScreen, BottomSheet, Banner, or Snackbar
    onRetry = { /* refresh logic */ },
) {
    NavHost(...) { ... }
}
```

---

## ⚡ 5. Automated Feature Scaffolding Task (`createFeature`)

The template provides a unified scaffolding Gradle task supporting both **Divided** (3 submodules) and **Compact** (single module) Clean Architecture feature patterns:

| Feature Type | Gradle Command | When to Use | Generated Structure |
|---|---|---|---|
| **Divided** *(Default)* | `./gradlew createFeature -Pname=<name>` | Complex business logic, checkout, payments, large domain entities | 3 submodules: `:domain`, `:data`, `:presentation` |
| **Compact** | `./gradlew createFeature -Pname=<name> -Ptype=compact` | Lightweight features, settings, profile, static/CRUD screens | 1 module: `:feature:<name>` with internal `domain/`, `data/`, `presentation/` packages |

---

### Option A: Divided Feature Mode (Default)

```bash
./gradlew createFeature -Pname=orders
```

#### Automated Actions Executed:
1. **Generates `:feature:orders:domain` submodule**:
   - `OrdersItem.kt` (domain model)
   - `OrdersRepository.kt` (repository interface)
   - `GetOrdersItemsUseCase.kt` (domain use case)
   - `build.gradle.kts` with `bedrock.kotlin.jvm` (pure Kotlin JVM, zero Android dependencies)
2. **Generates `:feature:orders:data` submodule**:
   - `OrdersDto.kt` (kotlinx.serialization `@Serializable` DTO with `.toDomain()` mapper)
   - `OrdersApiService.kt` (Retrofit service)
   - `OrdersRepositoryImpl.kt` (repository implementation using `safeApiCall`)
   - `OrdersDataModule.kt` (Hilt dependency injection module)
   - `build.gradle.kts` with `bedrock.android.library` and `bedrock.android.hilt`
3. **Generates `:feature:orders:presentation` submodule**:
   - `OrdersUiState.kt` (state holder leveraging `AsyncState<List<OrdersItem>>`)
   - `OrdersViewModel.kt` (`@HiltViewModel` with state flow and lifecycle handling)
   - `OrdersScreen.kt` (Material 3 Compose UI utilizing `AsyncContent`, skeleton shimmer loading, LazyColumn, and `@Preview`)
   - `OrdersFeatureEntryImpl.kt` (`FeatureEntry` multibinding implementation)
   - `OrdersNavigationModule.kt` (Hilt `@IntoSet` multibinding module)
   - `build.gradle.kts` with `bedrock.android.feature`
4. **Registers in `settings.gradle.kts`**:
   - Appends `include(":feature:orders:domain")`, `include(":feature:orders:data")`, `include(":feature:orders:presentation")`
5. **Wires into `app/build.gradle.kts`**:
   - Automatically injects `:feature:orders:presentation` and `:feature:orders:data` into the app `dependencies { ... }` block.

---

### Option B: Compact Feature Mode (`-Ptype=compact`)

```bash
./gradlew createFeature -Pname=settings -Ptype=compact
```

#### Automated Actions Executed:
1. **Generates single `:feature:settings` module**:
   - `bedrock.android.feature` convention plugin with resource prefix (`settings_`)
   - `domain/` package: `SettingsItem.kt`, `SettingsRepository.kt`, `GetSettingsItemsUseCase.kt`
   - `data/` package: `SettingsDto.kt`, `SettingsApiService.kt`, `SettingsRepositoryImpl.kt`, `SettingsDataModule.kt`
   - `presentation/` package: `SettingsUiState.kt`, `SettingsViewModel.kt`, `SettingsScreen.kt`, `SettingsFeatureEntryImpl.kt`, `SettingsNavigationModule.kt`
2. **Registers in `settings.gradle.kts`**:
   - Appends `include(":feature:settings")`
3. **Wires into `app/build.gradle.kts`**:
   - Injects `implementation(project(":feature:settings"))` into `app/build.gradle.kts`.

---

### Seamless Coexistence via `FeatureEntry`

Regardless of whether a feature was generated as **Divided** or **Compact**, both expose the identical contract:
```kotlin
// Both register their Jetpack Compose routes into Dagger Hilt's Set<FeatureEntry>
class OrdersFeatureEntryImpl @Inject constructor() : FeatureEntry { ... }
class SettingsFeatureEntryImpl @Inject constructor() : FeatureEntry { ... }
```
The `:app` module simply injects `Set<FeatureEntry>` and registers all routes into `NavHost`. Developers can choose divided or compact on a feature-by-feature basis without refactoring app navigation or build logic.

---

### Extensible Scaffolding Architecture (`build-logic`)

All code generation and scaffolding logic is decoupled from the root `build.gradle.kts` (reducing it from 704 lines down to **43 lines**) and modularized into dedicated components under `build-logic/convention/`:

```
build-logic/convention/src/main/kotlin/govi/buildlogic/scaffolding/
├── ScaffoldingPlugin.kt              # Plugin registering createFeature (and future generator tasks)
├── CreateFeatureTask.kt              # Task orchestrating feature directory generation & project wiring
└── templates/
    ├── BuildScriptTemplates.kt       # Gradle build script templates (compact, domain, data, presentation)
    ├── DomainTemplates.kt            # Domain Model, Repository, and UseCase templates
    ├── DataTemplates.kt              # Retrofit DTO, ApiService, RepositoryImpl, and DataModule templates
    └── PresentationTemplates.kt      # UiState, ViewModel, Compose Screen, FeatureEntry, and DiModule templates
```

**Adding Future Generators**:
To add new scaffolding tasks (such as `createUseCase`, `createRepository`, or `createDemoApp`):
1. Create a new task class in `govi/buildlogic/scaffolding/`
2. Add corresponding template generators under `govi/buildlogic/scaffolding/templates/`
3. Register the task in [`ScaffoldingPlugin.kt`](file:///Users/tejendranathreddy/AndroidStudioProjects/clean-architecture-template/build-logic/convention/src/main/kotlin/govi/buildlogic/scaffolding/ScaffoldingPlugin.kt).

---

## 🛡️ 6. Code Quality, Spotless & Git Pre-Commit Hook

The repository enforces a strict **Zero-Warning Policy** and automated code formatting:

### 1. Zero-Warning Policy (`-Werror`)
Every module compiles with `allWarningsAsErrors.set(true)`. Any unused variable, deprecated call, or compiler warning immediately fails compilation.

### 2. Auto-Formatting via Spotless (`ktlint`)
All Kotlin files and Gradle scripts follow the official Kotlin style guide via Spotless:
* Format code:
  ```bash
  ./gradlew spotlessApply
  ```
* Verify code format:
  ```bash
  ./gradlew spotlessCheck
  ```

### 3. Automated Git Pre-Commit Hook (`.githooks/pre-commit`)
A checked-in pre-commit hook automatically intercepts `git commit`:
1. Runs `spotlessApply` to format any staged code in-place.
2. Automatically re-stages modified files.
3. Enforces compilation under the Zero-Warning Policy (`allWarningsAsErrors = true`).
4. Rejects the commit if any warnings or compilation errors are detected.

To configure git hooks on any machine or CI:
```bash
./gradlew installGitHooks
```

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
