package com.govi.androidbedrock.core.utils.provider

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for providing current time, allowing for easy mocking in tests.
 */
interface TimeProvider {
    fun currentTimeMillis(): Long
}

@Singleton
class DefaultTimeProvider @Inject constructor() : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}
