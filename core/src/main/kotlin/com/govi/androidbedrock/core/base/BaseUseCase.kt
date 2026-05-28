package com.govi.androidbedrock.core.base

import kotlinx.coroutines.flow.Flow

/**
 * Base UseCase for all use cases
 * Handles business logic with safe execution patterns
 */
abstract class BaseUseCase<in Params, out Result> {

    /**
     * Execute the use case synchronously (suspend function)
     */
    suspend operator fun invoke(params: Params): Result {
        return execute(params)
    }

    /**
     * Override this method to implement the use case logic
     */
    protected abstract suspend fun execute(params: Params): Result
}

/**
 * Base UseCase for Flow-based operations
 */
abstract class BaseFlowUseCase<in Params, out Result> {

    /**
     * Execute the use case as a Flow
     */
    operator fun invoke(params: Params): Flow<Result> {
        return execute(params)
    }

    /**
     * Override this method to implement the Flow logic
     */
    protected abstract fun execute(params: Params): Flow<Result>
}

/**
 * Base UseCase for operations that don't require parameters
 */
abstract class BaseUseCaseNoParams<out Result> {

    /**
     * Execute the use case synchronously (suspend function)
     */
    suspend operator fun invoke(): Result {
        return execute()
    }

    /**
     * Override this method to implement the use case logic
     */
    protected abstract suspend fun execute(): Result
}

/**
 * Base UseCase for Flow-based operations without parameters
 */
abstract class BaseFlowUseCaseNoParams<out Result> {

    /**
     * Execute the use case as a Flow
     */
    operator fun invoke(): Flow<Result> {
        return execute()
    }

    /**
     * Override this method to implement the Flow logic
     */
    protected abstract fun execute(): Flow<Result>
}


/**
 * Result wrapper for use cases
 */
sealed class UseCaseResult<out T> {
    data class Success<T>(val data: T) : UseCaseResult<T>()
    data class Error(val exception: Exception) : UseCaseResult<Nothing>()
    class Loading : UseCaseResult<Nothing>()
}
