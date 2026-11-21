package com.govi.androidbedrock.feature.demo.presentation.detail

import com.govi.androidbedrock.core.base.mvi.UiEffect
import com.govi.androidbedrock.core.base.mvi.UiIntent
import com.govi.androidbedrock.core.base.mvi.UiState
import com.govi.androidbedrock.feature.demo.domain.model.DemoItem

data class DemoDetailUiState(
    val optionId: String = "",
    val demoItem: DemoItem? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Interactive states for UI demo
    val buttonClickCount: Int = 0,
    val isButtonLoading: Boolean = false,
    val inputSampleText: String = "",
    val passwordSampleText: String = "",
    val isShimmerActive: Boolean = false,
    // Interactive states for Network demo
    val simulatedHttpCode: Int = 200,
    val simulatedErrorMessage: String = "",
    val simulatedParsedError: String = "",
    // Interactive states for DataStore demo
    val inputAccessToken: String = "sample_access_token_12345",
    val inputRefreshToken: String = "sample_refresh_token_67890",
    val currentSavedToken: String? = null,
    // Interactive states for Telemetry demo
    val telemetryLog: List<String> = emptyList(),
    // Interactive states for Offline demo
    val activeOfflineFormat: String = "None",
    // Interactive states for MVI demo
    val mviCounter: Int = 0,
    val isMviAsyncRunning: Boolean = false,
) : UiState

sealed interface DemoDetailIntent : UiIntent {
    data class LoadOption(
        val optionId: String,
    ) : DemoDetailIntent

    data object BackClicked : DemoDetailIntent

    // UI actions
    data object IncrementButtonClick : DemoDetailIntent

    data object ToggleButtonLoading : DemoDetailIntent

    data class UpdateInputText(
        val text: String,
    ) : DemoDetailIntent

    data class UpdatePasswordText(
        val text: String,
    ) : DemoDetailIntent

    data object ToggleShimmer : DemoDetailIntent

    // Network actions
    data class SimulateHttpCode(
        val code: Int,
    ) : DemoDetailIntent

    data class SimulateJsonError(
        val jsonPayload: String,
    ) : DemoDetailIntent

    // DataStore actions
    data class UpdateTokenInputs(
        val accessToken: String,
        val refreshToken: String,
    ) : DemoDetailIntent

    data object SaveTokenToDataStore : DemoDetailIntent

    data object ClearTokenFromDataStore : DemoDetailIntent

    // Telemetry actions
    data class LogAnalyticsEvent(
        val name: String,
        val params: Map<String, String>,
    ) : DemoDetailIntent

    data class RecordCrashlyticsError(
        val errorText: String,
    ) : DemoDetailIntent

    // Offline actions
    data class SelectOfflineFormat(
        val format: String,
    ) : DemoDetailIntent

    // MVI actions
    data object IncrementMviCounter : DemoDetailIntent

    data object TriggerMviAsync : DemoDetailIntent
}

sealed interface DemoDetailEffect : UiEffect {
    data object NavigateBack : DemoDetailEffect

    data class ShowSnackbar(
        val message: String,
    ) : DemoDetailEffect
}
