package govi.core.base.usecase

import govi.core.base.dispatcher.AppDispatchers
import govi.core.base.result.AppError
import govi.core.base.result.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BaseUseCaseTest {
    class TestDispatchers(
        private val dispatcher: CoroutineDispatcher,
    ) : AppDispatchers {
        override val io: CoroutineDispatcher = dispatcher
        override val default: CoroutineDispatcher = dispatcher
        override val main: CoroutineDispatcher = dispatcher
        override val mainImmediate: CoroutineDispatcher = dispatcher
        override val unconfined: CoroutineDispatcher = dispatcher
    }

    class SuccessfulUseCase(
        dispatchers: AppDispatchers,
    ) : BaseUseCase<Int, String>(dispatchers) {
        override suspend fun execute(parameters: Int): AppResult<String> = AppResult.Success("Processed: $parameters")
    }

    class FailingUseCase(
        dispatchers: AppDispatchers,
    ) : BaseUseCase<Unit, String>(dispatchers) {
        override suspend fun execute(parameters: Unit): AppResult<String> = throw IllegalStateException("Database query failed")
    }

    class CancellingUseCase(
        dispatchers: AppDispatchers,
    ) : BaseUseCase<Unit, String>(dispatchers) {
        override suspend fun execute(parameters: Unit): AppResult<String> = throw kotlinx.coroutines.CancellationException("Job cancelled")
    }

    class StreamUseCase(
        dispatchers: AppDispatchers,
    ) : FlowUseCase<String, String>(dispatchers) {
        override fun execute(parameters: String): Flow<AppResult<String>> = flowOf(AppResult.Success("Streamed: $parameters"))
    }

    @Test
    fun `suspending usecase executes successfully on dispatcher`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val useCase = SuccessfulUseCase(TestDispatchers(testDispatcher))

            val result = useCase(42)
            assertTrue(result is AppResult.Success)
            assertEquals("Processed: 42", (result as AppResult.Success).data)
        }

    @Test
    fun `suspending usecase catches uncaught exceptions into AppError Unknown`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val useCase = FailingUseCase(TestDispatchers(testDispatcher))

            val result = useCase(Unit)
            assertTrue(result is AppResult.Error)
            val error = (result as AppResult.Error).error
            assertTrue(error is AppError.Unknown)
            assertEquals("Database query failed", error.message)
        }

    @Test(expected = kotlinx.coroutines.CancellationException::class)
    fun `suspending usecase rethrows cancellation exception`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val useCase = CancellingUseCase(TestDispatchers(testDispatcher))
            useCase(Unit)
        }

    @Test
    fun `flow usecase emits stream results successfully`() =
        runTest {
            val testDispatcher = StandardTestDispatcher(testScheduler)
            val useCase = StreamUseCase(TestDispatchers(testDispatcher))

            val result = useCase("item").first()
            assertTrue(result is AppResult.Success)
            assertEquals("Streamed: item", (result as AppResult.Success).data)
        }
}
