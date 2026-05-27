package com.example.androidbedrock.core.base

import com.example.androidbedrock.core.network.ApiResult
import kotlinx.coroutines.flow.Flow

/**
 * Base UseCase for all use cases
 * Handles business logic with safe/unsafe execution patterns
 */
abstract class BaseUseCase<in Params, out Result> {

    /**
     * Execute the use case synchronously (suspend function)
     */
    suspend operator fun invoke(params: Params): Result {
        return execute(params)
    }

    /**
     * Execute the use case asynchronously (Flow)
     */
    open operator fun invoke(params: Params): Flow<Result> {
        throw NotImplementedError("Either override invoke() or implement Flow version")
    }

    /**
     * Override this method to implement the use case logic
     */
    protected abstract suspend fun execute(params: Params): Result
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
     * Execute the use case asynchronously (Flow)
     */
    open operator fun invoke(): Flow<Result> {
        throw NotImplementedError("Either override invoke() or implement Flow version")
    }

    /**
     * Override this method to implement the use case logic
     */
    protected abstract suspend fun execute(): Result
}

/**
 * Result wrapper for use cases
 */
seal class UseCaseResult<out T> {
    data class Success<T>(val data: T) : UseCaseResult<T>()
    data class Error(val exception: Exception) : UseCaseResult<Nothing>()
    class Loading : UseCaseResult<Nothing>()
}
