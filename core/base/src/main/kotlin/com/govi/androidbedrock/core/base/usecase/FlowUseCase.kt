package com.govi.androidbedrock.core.base.usecase

import com.govi.androidbedrock.core.base.dispatcher.AppDispatchers
import com.govi.androidbedrock.core.base.result.AppError
import com.govi.androidbedrock.core.base.result.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

/**
 * Reactive Use Case base class executing on a background dispatcher with safe error catching.
 */
abstract class FlowUseCase<in P, out R>(
    private val dispatchers: AppDispatchers,
) {
    operator fun invoke(parameters: P): Flow<AppResult<R>> =
        execute(parameters)
            .catch { e ->
                if (e is CancellationException) {
                    throw e
                }
                emit(
                    AppResult.Error(
                        AppError.Unknown(
                            message = e.message ?: "Stream error occurred",
                            cause = e,
                        ),
                    ),
                )
            }.flowOn(dispatchers.io)

    protected abstract fun execute(parameters: P): Flow<AppResult<R>>
}
