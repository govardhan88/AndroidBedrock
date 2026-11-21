package com.govi.androidbedrock.feature.demo.presentation.detail

import androidx.lifecycle.SavedStateHandle
import com.govi.androidbedrock.core.datastore.TokenDataStore
import com.govi.androidbedrock.core.firebase.analytics.AnalyticsTracker
import com.govi.androidbedrock.core.firebase.crashlytics.CrashReporter
import com.govi.androidbedrock.core.navigation.AppNavigator
import com.govi.androidbedrock.core.navigation.NavigationIntent
import com.govi.androidbedrock.core.network.connectivity.NetworkMonitor
import com.govi.androidbedrock.feature.demo.data.repository.DemoRepositoryImpl
import com.govi.androidbedrock.feature.demo.domain.usecase.GetDemoItemByIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DemoDetailViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var navigator: TestAppNavigator
    private lateinit var tokenDataStore: FakeTokenDataStore
    private lateinit var networkMonitor: FakeNetworkMonitor
    private lateinit var analyticsTracker: FakeAnalyticsTracker
    private lateinit var crashReporter: FakeCrashReporter
    private lateinit var repository: DemoRepositoryImpl
    private lateinit var getItemByIdUseCase: GetDemoItemByIdUseCase
    private lateinit var viewModel: DemoDetailViewModel

    class TestAppNavigator : AppNavigator {
        override val navigationChannel: Flow<NavigationIntent> = emptyFlow()
        var backPressed: Boolean = false

        override fun navigateTo(
            route: Any,
            popUpToRoute: Any?,
            inclusive: Boolean,
            isSingleTop: Boolean,
        ) {}

        override fun navigateBack() {
            backPressed = true
        }

        override fun popTo(
            route: Any,
            inclusive: Boolean,
        ) {}
    }

    class FakeNetworkMonitor : NetworkMonitor {
        override val isOnline: Flow<Boolean> = MutableStateFlow(true)
        override val isCurrentlyOnline: Boolean = true
    }

    class FakeTokenDataStore : TokenDataStore {
        val accessFlow = MutableStateFlow<String?>("initial-access-token")
        val refreshFlow = MutableStateFlow<String?>("initial-refresh-token")

        override val accessTokenFlow: Flow<String?> = accessFlow
        override val refreshTokenFlow: Flow<String?> = refreshFlow

        var savedAccess: String? = null
        var savedRefresh: String? = null

        override suspend fun getAccessToken(): String? = accessFlow.value

        override suspend fun getRefreshToken(): String? = refreshFlow.value

        override suspend fun saveTokens(
            accessToken: String,
            refreshToken: String,
        ) {
            savedAccess = accessToken
            savedRefresh = refreshToken
            accessFlow.value = accessToken
            refreshFlow.value = refreshToken
        }

        override suspend fun clearTokens() {
            savedAccess = null
            savedRefresh = null
            accessFlow.value = null
            refreshFlow.value = null
        }
    }

    class FakeAnalyticsTracker : AnalyticsTracker {
        val loggedEvents = mutableListOf<Pair<String, Map<String, Any?>>>()

        override fun logEvent(
            name: String,
            params: Map<String, Any?>,
        ) {
            loggedEvents.add(name to params)
        }

        override fun setUserProperty(
            name: String,
            value: String?,
        ) {}

        override fun setUserId(userId: String?) {}

        override fun resetAnalyticsData() {}
    }

    class FakeCrashReporter : CrashReporter {
        val loggedExceptions = mutableListOf<Throwable>()
        val customKeys = mutableMapOf<String, Any>()

        override fun recordException(throwable: Throwable) {
            loggedExceptions.add(throwable)
        }

        override fun log(message: String) {}

        override fun setUserId(userId: String) {}

        override fun setCustomKey(
            key: String,
            value: String,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Boolean,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Int,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Long,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Double,
        ) {
            customKeys[key] = value
        }

        override fun setCustomKey(
            key: String,
            value: Float,
        ) {
            customKeys[key] = value
        }

        override fun setCrashlyticsCollectionEnabled(enabled: Boolean) {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        navigator = TestAppNavigator()
        tokenDataStore = FakeTokenDataStore()
        networkMonitor = FakeNetworkMonitor()
        analyticsTracker = FakeAnalyticsTracker()
        crashReporter = FakeCrashReporter()
        repository = DemoRepositoryImpl()
        getItemByIdUseCase = GetDemoItemByIdUseCase(repository)

        viewModel =
            DemoDetailViewModel(
                savedStateHandle = SavedStateHandle(),
                getItemByIdUseCase = getItemByIdUseCase,
                appNavigator = navigator,
                networkMonitor = networkMonitor,
                tokenDataStore = tokenDataStore,
                analyticsTracker = analyticsTracker,
                crashReporter = crashReporter,
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadOption loads option successfully`() =
        runTest {
            viewModel.loadOption("ui-components")
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals("ui-components", state.optionId)
            assertNotNull(state.demoItem)
            assertEquals("Design System & UI Components", state.demoItem?.title)
        }

    @Test
    fun `IncrementButtonClick updates buttonClickCount`() =
        runTest {
            viewModel.processIntent(DemoDetailIntent.IncrementButtonClick)
            assertEquals(1, viewModel.uiState.value.buttonClickCount)

            viewModel.processIntent(DemoDetailIntent.IncrementButtonClick)
            assertEquals(2, viewModel.uiState.value.buttonClickCount)
        }

    @Test
    fun `SimulateHttpCode updates state with mapped error message`() =
        runTest {
            viewModel.processIntent(DemoDetailIntent.SimulateHttpCode(404))
            assertEquals(404, viewModel.uiState.value.simulatedHttpCode)
            assertTrue(
                viewModel.uiState.value.simulatedErrorMessage
                    .contains("not found", ignoreCase = true),
            )
        }

    @Test
    fun `SimulateJsonError parses error message from json payload`() =
        runTest {
            val sampleJson = """{"message": "Invalid credentials provided"}"""
            viewModel.processIntent(DemoDetailIntent.SimulateJsonError(sampleJson))

            assertEquals(422, viewModel.uiState.value.simulatedHttpCode)
            assertEquals("Invalid credentials provided", viewModel.uiState.value.simulatedErrorMessage)
        }

    @Test
    fun `SaveTokenToDataStore and ClearTokenFromDataStore update DataStore`() =
        runTest {
            viewModel.processIntent(DemoDetailIntent.UpdateTokenInputs("token123", "refresh123"))
            viewModel.processIntent(DemoDetailIntent.SaveTokenToDataStore)
            advanceUntilIdle()

            assertEquals("token123", tokenDataStore.savedAccess)
            assertEquals("refresh123", tokenDataStore.savedRefresh)

            viewModel.processIntent(DemoDetailIntent.ClearTokenFromDataStore)
            advanceUntilIdle()

            assertEquals(null, tokenDataStore.savedAccess)
        }

    @Test
    fun `LogAnalyticsEvent logs event to tracker and updates telemetryLog`() =
        runTest {
            val params = mapOf("screen" to "demo")
            viewModel.processIntent(DemoDetailIntent.LogAnalyticsEvent("test_event", params))

            assertEquals(1, analyticsTracker.loggedEvents.size)
            assertEquals("test_event", analyticsTracker.loggedEvents.first().first)
            assertTrue(
                viewModel.uiState.value.telemetryLog
                    .any { it.contains("test_event") },
            )
        }

    @Test
    fun `RecordCrashlyticsError records exception and updates telemetryLog`() =
        runTest {
            viewModel.processIntent(DemoDetailIntent.RecordCrashlyticsError("Sample Test Error"))

            assertEquals(1, crashReporter.loggedExceptions.size)
            assertTrue(
                viewModel.uiState.value.telemetryLog
                    .any { it.contains("Sample Test Error") },
            )
        }

    @Test
    fun `BackClicked intent delegates to navigator`() =
        runTest {
            viewModel.processIntent(DemoDetailIntent.BackClicked)
            assertTrue(navigator.backPressed)
        }

    @Test
    fun `IncrementMviCounter increases mviCounter`() =
        runTest {
            viewModel.processIntent(DemoDetailIntent.IncrementMviCounter)
            assertEquals(1, viewModel.uiState.value.mviCounter)
        }
}
