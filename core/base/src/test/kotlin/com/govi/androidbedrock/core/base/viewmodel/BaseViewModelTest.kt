package com.govi.androidbedrock.core.base.viewmodel

import com.govi.androidbedrock.core.base.mvi.UiEffect
import com.govi.androidbedrock.core.base.mvi.UiIntent
import com.govi.androidbedrock.core.base.mvi.UiState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BaseViewModelTest {
    data class TestState(
        val count: Int = 0,
    ) : UiState

    sealed interface TestIntent : UiIntent {
        data object Increment : TestIntent

        data class ShowSnackbar(
            val message: String,
        ) : TestIntent
    }

    sealed interface TestEffect : UiEffect {
        data class Toast(
            val message: String,
        ) : TestEffect
    }

    class TestViewModel : BaseViewModel<TestState, TestIntent, TestEffect>(TestState()) {
        override fun processIntent(intent: TestIntent) {
            when (intent) {
                TestIntent.Increment -> updateState { copy(count = count + 1) }
                is TestIntent.ShowSnackbar -> sendEffect(TestEffect.Toast(intent.message))
            }
        }
    }

    @Test
    fun `initial state is exposed correctly`() {
        val viewModel = TestViewModel()
        assertEquals(0, viewModel.currentState.count)
        assertEquals(0, viewModel.uiState.value.count)
    }

    @Test
    fun `processIntent updates state synchronously`() {
        val viewModel = TestViewModel()
        viewModel.processIntent(TestIntent.Increment)

        assertEquals(1, viewModel.currentState.count)
        assertEquals(1, viewModel.uiState.value.count)
    }

    @Test
    fun `sendEffect emits effect to effect channel`() =
        runTest {
            val viewModel = TestViewModel()
            viewModel.processIntent(TestIntent.ShowSnackbar("Action completed"))

            val effect = viewModel.effect.first()
            assertEquals(TestEffect.Toast("Action completed"), effect)
        }
}
