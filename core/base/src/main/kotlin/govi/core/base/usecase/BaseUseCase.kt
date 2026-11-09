package govi.core.base.usecase

import govi.core.base.dispatcher.AppDispatchers
import govi.core.base.result.AppError
import govi.core.base.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

/**
 * Suspending Use Case base class executing business logic on the background dispatcher
 * with structured exception wrapping into [AppResult].
 */
abstract class BaseUseCase<in P, out R>(
    private val dispatchers: AppDispatchers,
) {
    suspend operator fun invoke(parameters: P): AppResult<R> =
        withContext(dispatchers.io) {
            try {
                execute(parameters)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppResult.Error(
                    AppError.Unknown(
                        message = e.message ?: "An unexpected error occurred",
                        cause = e,
                    ),
                )
            }
        }

    protected abstract suspend fun execute(parameters: P): AppResult<R>
}
