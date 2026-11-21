package com.govi.androidbedrock.feature.demo.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.govi.androidbedrock.core.base.viewmodel.BaseViewModel
import com.govi.androidbedrock.core.datastore.TokenDataStore
import com.govi.androidbedrock.core.firebase.analytics.AnalyticsTracker
import com.govi.androidbedrock.core.firebase.crashlytics.CrashReporter
import com.govi.androidbedrock.core.navigation.AppNavigator
import com.govi.androidbedrock.core.navigation.route.AppRoute
import com.govi.androidbedrock.core.network.connectivity.NetworkMonitor
import com.govi.androidbedrock.core.network.error.HttpErrorMessages
import com.govi.androidbedrock.core.network.error.RemoteErrorParser
import com.govi.androidbedrock.feature.demo.domain.usecase.GetDemoItemByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DemoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getItemByIdUseCase: GetDemoItemByIdUseCase,
    private val appNavigator: AppNavigator,
    val networkMonitor: NetworkMonitor,
    private val tokenDataStore: TokenDataStore,
    private val analyticsTracker: AnalyticsTracker,
    private val crashReporter: CrashReporter,
) : BaseViewModel<DemoDetailUiState, DemoDetailIntent, DemoDetailEffect>(DemoDetailUiState()) {
    init {
        val route = runCatching { savedStateHandle.toRoute<AppRoute.DemoDetail>() }.getOrNull()
        val optionId = route?.optionId ?: ""
        if (optionId.isNotBlank()) {
            loadOption(optionId)
        }
        observeSavedToken()
    }

    override fun processIntent(intent: DemoDetailIntent) {
        when (intent) {
            is DemoDetailIntent.LoadOption -> loadOption(intent.optionId)
            is DemoDetailIntent.BackClicked -> onBackClicked()
            is DemoDetailIntent.IncrementButtonClick -> onIncrementButtonClick()
            is DemoDetailIntent.ToggleButtonLoading -> onToggleButtonLoading()
            is DemoDetailIntent.UpdateInputText -> updateState { copy(inputSampleText = intent.text) }
            is DemoDetailIntent.UpdatePasswordText -> updateState { copy(passwordSampleText = intent.text) }
            is DemoDetailIntent.ToggleShimmer -> updateState { copy(isShimmerActive = !isShimmerActive) }
            is DemoDetailIntent.SimulateHttpCode -> onSimulateHttpCode(intent.code)
            is DemoDetailIntent.SimulateJsonError -> onSimulateJsonError(intent.jsonPayload)
            is DemoDetailIntent.UpdateTokenInputs ->
                updateState {
                    copy(
                        inputAccessToken = intent.accessToken,
                        inputRefreshToken = intent.refreshToken,
                    )
                }
            is DemoDetailIntent.SaveTokenToDataStore -> onSaveToken()
            is DemoDetailIntent.ClearTokenFromDataStore -> onClearToken()
            is DemoDetailIntent.LogAnalyticsEvent -> onLogAnalytics(intent.name, intent.params)
            is DemoDetailIntent.RecordCrashlyticsError -> onRecordCrashlytics(intent.errorText)
            is DemoDetailIntent.SelectOfflineFormat -> updateState { copy(activeOfflineFormat = intent.format) }
            is DemoDetailIntent.IncrementMviCounter -> updateState { copy(mviCounter = mviCounter + 1) }
            is DemoDetailIntent.TriggerMviAsync -> onTriggerMviAsync()
        }
    }

    fun loadOption(optionId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, optionId = optionId) }
            getItemByIdUseCase(optionId).fold(
                onSuccess = { item ->
                    updateState { copy(isLoading = false, demoItem = item) }
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Failed to load demo option",
                        )
                    }
                },
            )
        }
    }

    private fun observeSavedToken() {
        viewModelScope.launch {
            combine(
                tokenDataStore.accessTokenFlow,
                tokenDataStore.refreshTokenFlow,
            ) { access, refresh ->
                if (access != null) {
                    "Access: ${access.take(15)}... | Refresh: ${refresh?.take(15)}..."
                } else {
                    "None (Not Logged In)"
                }
            }.collect { formattedToken ->
                updateState { copy(currentSavedToken = formattedToken) }
            }
        }
    }

    private fun onIncrementButtonClick() {
        updateState { copy(buttonClickCount = buttonClickCount + 1) }
        sendEffect(DemoDetailEffect.ShowSnackbar("Button clicked! Current count: ${currentState.buttonClickCount}"))
    }

    private fun onToggleButtonLoading() {
        viewModelScope.launch {
            updateState { copy(isButtonLoading = true) }
            delay(1500)
            updateState { copy(isButtonLoading = false) }
            sendEffect(DemoDetailEffect.ShowSnackbar("Loading simulation finished"))
        }
    }

    private fun onSimulateHttpCode(code: Int) {
        val userMessage = HttpErrorMessages.getMessage(code)
        val parsedType =
            when (code) {
                400 -> "AppError.Validation (Bad Request)"
                401 -> "AppError.Network.Unauthorized (Token Expired)"
                403 -> "AppError.Network.AccessDenied (Forbidden)"
                404 -> "AppError.Network.NotFound"
                409 -> "AppError.Network.Conflict"
                429 -> "AppError.Network.RateLimitExceeded"
                500 -> "AppError.Network.ServerError (Internal Error)"
                503 -> "AppError.Network.ServiceUnavailable"
                else -> "AppError.Network.ClientError"
            }
        updateState {
            copy(
                simulatedHttpCode = code,
                simulatedErrorMessage = userMessage,
                simulatedParsedError = parsedType,
            )
        }
    }

    private fun onSimulateJsonError(jsonPayload: String) {
        val parsedMsg = RemoteErrorParser.parse(jsonPayload)?.message ?: "No error parsed"
        updateState {
            copy(
                simulatedHttpCode = 422,
                simulatedErrorMessage = parsedMsg,
                simulatedParsedError = "RemoteErrorParser.parse()",
            )
        }
        sendEffect(DemoDetailEffect.ShowSnackbar("Parsed message: $parsedMsg"))
    }

    private fun onSaveToken() {
        viewModelScope.launch {
            tokenDataStore.saveTokens(
                accessToken = currentState.inputAccessToken,
                refreshToken = currentState.inputRefreshToken,
            )
            sendEffect(DemoDetailEffect.ShowSnackbar("Auth Tokens saved to DataStore successfully!"))
        }
    }

    private fun onClearToken() {
        viewModelScope.launch {
            tokenDataStore.clearTokens()
            sendEffect(DemoDetailEffect.ShowSnackbar("Auth Tokens cleared from DataStore."))
        }
    }

    private fun onLogAnalytics(
        name: String,
        params: Map<String, String>,
    ) {
        analyticsTracker.logEvent(name, params)
        val logEntry = "[Analytics] Event: '$name' params=$params"
        updateState { copy(telemetryLog = listOf(logEntry) + telemetryLog) }
        sendEffect(DemoDetailEffect.ShowSnackbar("Analytics event '$name' logged!"))
    }

    private fun onRecordCrashlytics(errorText: String) {
        crashReporter.setCustomKey("demo_source", "DemoDetailScreen")
        crashReporter.recordException(RuntimeException("Bedrock Demo Non-Fatal Exception: $errorText"))
        val logEntry = "[Crashlytics] Recorded Non-Fatal: '$errorText'"
        updateState { copy(telemetryLog = listOf(logEntry) + telemetryLog) }
        sendEffect(DemoDetailEffect.ShowSnackbar("Crashlytics non-fatal exception recorded!"))
    }

    private fun onTriggerMviAsync() {
        viewModelScope.launch {
            updateState { copy(isMviAsyncRunning = true) }
            delay(1200)
            updateState {
                copy(
                    isMviAsyncRunning = false,
                    mviCounter = mviCounter + 10,
                )
            }
            sendEffect(DemoDetailEffect.ShowSnackbar("MVI Async task completed! Counter +10"))
        }
    }

    fun onBackClicked() {
        appNavigator.navigateBack()
    }
}
