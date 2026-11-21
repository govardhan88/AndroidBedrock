package com.govi.androidbedrock.feature.demo.data.repository

import com.govi.androidbedrock.feature.demo.domain.model.DemoItem
import com.govi.androidbedrock.feature.demo.domain.repository.DemoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoRepositoryImpl @Inject constructor() : DemoRepository {
    private val demoCatalog =
        listOf(
            DemoItem(
                id = "ui-components",
                title = "Design System & UI Components",
                description =
                    "Interactive Material 3 components: buttons with loading/disabled states, " +
                        "text fields with validation, cards, badges, and shimmer loaders.",
                category = "Presentation",
                badgeText = "Compose",
                features =
                    listOf(
                        "AppButton variants (Primary, Secondary, Outlined, Danger)",
                        "AppTextField & AppPasswordTextField with live validation",
                        "Card variants (Elevated, Outlined, Filled)",
                        "Status badges (Success, Warning, Error, Info)",
                        "Shimmer effect skeleton loaders",
                    ),
            ),
            DemoItem(
                id = "network-resilience",
                title = "Network Resilience & Error Parsing",
                description =
                    "Simulate HTTP status codes, test HTTP/2 empty reason fallback, " +
                        "and verify backend error JSON parsing into user-friendly AppError instances.",
                category = "Core Network",
                badgeText = "SafeApiCall",
                features =
                    listOf(
                        "Standard user messages for HTTP 400-599",
                        "HTTP/2 blank status reason fallback",
                        "Backend error JSON payload parsing",
                        "SafeApiCall conversion to AppResult & AppError",
                    ),
            ),
            DemoItem(
                id = "offline-detection",
                title = "Connectivity Monitor & Offline Formats",
                description =
                    "Real-time network state monitoring with interactive previews of " +
                        "Bedrock's 4 offline UI formats (Dialog, Banner, BottomSheet, Snackbar).",
                category = "Connectivity",
                badgeText = "NetworkMonitor",
                features =
                    listOf(
                        "Real-time network status Flow",
                        "Interactive switch between 4 offline UI formats",
                        "System wireless settings intent launcher",
                        "Automatic reconnect observer",
                    ),
            ),
            DemoItem(
                id = "secure-datastore",
                title = "Encrypted Token DataStore",
                description =
                    "Test token storage operations with Jetpack DataStore: save, inspect, " +
                        "and clear auth credentials with reactive Flow observation.",
                category = "Storage",
                badgeText = "DataStore",
                features =
                    listOf(
                        "Secure Access & Refresh token persistence",
                        "Live reactive StateFlow inspection",
                        "Atomic save and clear token actions",
                        "Thread-safe coroutine storage",
                    ),
            ),
            DemoItem(
                id = "telemetry",
                title = "Analytics Tracker & Crash Reporting",
                description =
                    "Live event dispatching with key-value parameters and non-fatal " +
                        "Crashlytics error logging with diagnostic metadata.",
                category = "Monitoring",
                badgeText = "Telemetry",
                features =
                    listOf(
                        "Structured analytics events with parameters",
                        "Non-fatal exception capture",
                        "Crashlytics custom key-value attributes",
                        "Production vs NoOp tracker switching",
                    ),
            ),
            DemoItem(
                id = "mvi-architecture",
                title = "MVI Unidirectional Data Flow",
                description =
                    "Experience Bedrock's MVI pattern live: dispatch user intents, " +
                        "observe immutable state transitions, and handle one-off side effects.",
                category = "Architecture",
                badgeText = "MVI",
                features =
                    listOf(
                        "Strict unidirectional data flow",
                        "Immutable UiState stream",
                        "Single-event Channel for side effects (UiEffect)",
                        "Reactive BaseViewModel processing",
                    ),
            ),
        )

    override suspend fun getItems(): Result<List<DemoItem>> = Result.success(demoCatalog)

    override suspend fun getItemById(id: String): Result<DemoItem> =
        demoCatalog.find { it.id == id }?.let { Result.success(it) }
            ?: Result.failure(NoSuchElementException("Demo option not found: $id"))
}
