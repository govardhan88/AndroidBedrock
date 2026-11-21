package com.govi.androidbedrock.feature.demo.domain

import com.govi.androidbedrock.feature.demo.data.repository.DemoRepositoryImpl
import com.govi.androidbedrock.feature.demo.domain.usecase.GetDemoItemByIdUseCase
import com.govi.androidbedrock.feature.demo.domain.usecase.GetDemoItemsUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetDemoItemsUseCaseTest {
    private lateinit var repository: DemoRepositoryImpl
    private lateinit var getDemoItemsUseCase: GetDemoItemsUseCase
    private lateinit var getDemoItemByIdUseCase: GetDemoItemByIdUseCase

    @Before
    fun setUp() {
        repository = DemoRepositoryImpl()
        getDemoItemsUseCase = GetDemoItemsUseCase(repository)
        getDemoItemByIdUseCase = GetDemoItemByIdUseCase(repository)
    }

    @Test
    fun `getDemoItems returns complete list of demo options`() =
        runTest {
            val result = getDemoItemsUseCase()
            assertTrue(result.isSuccess)
            val items = result.getOrThrow()
            assertEquals(6, items.size)
            assertTrue(items.any { it.id == "ui-components" })
            assertTrue(items.any { it.id == "network-resilience" })
            assertTrue(items.any { it.id == "offline-detection" })
            assertTrue(items.any { it.id == "secure-datastore" })
            assertTrue(items.any { it.id == "telemetry" })
            assertTrue(items.any { it.id == "mvi-architecture" })
        }

    @Test
    fun `getDemoItemById returns item when id exists`() =
        runTest {
            val result = getDemoItemByIdUseCase("ui-components")
            assertTrue(result.isSuccess)
            val item = result.getOrThrow()
            assertEquals("ui-components", item.id)
            assertEquals("Design System & UI Components", item.title)
        }

    @Test
    fun `getDemoItemById returns failure when id does not exist`() =
        runTest {
            val result = getDemoItemByIdUseCase("non_existent_id")
            assertTrue(result.isFailure)
        }
}
