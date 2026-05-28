package com.govi.androidbedrock.demo.data

import com.govi.androidbedrock.core.base.BaseRepository
import com.govi.androidbedrock.core.network.ApiException
import com.govi.androidbedrock.core.network.ApiResult
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoRepository @Inject constructor() : BaseRepository() {

    /**
     * Simulates an API call that returns a list of demo items
     */
    suspend fun getDemoItems(): ApiResult<List<DemoItem>> {
        return safeApiCall {
            // Simulate network delay
            delay(1000)
            
            // Mock data
            listOf(
                DemoItem(1, "Clean Architecture", "Layers: App, Domain, Core"),
                DemoItem(2, "Hilt DI", "Simplified dependency injection"),
                DemoItem(3, "Retrofit + Serialization", "Typed networking with Kotlin Serialization"),
                DemoItem(4, "Base Components", "Ready-to-use BaseViewModel and BaseRepository")
            )
        }
    }

    /**
     * Simulates a network error
     */
    suspend fun getMockError(): ApiResult<List<DemoItem>> {
        return safeApiCall {
            delay(1000)
            throw ApiException.HttpException(404, "Mocked 404 Not Found")
        }
    }
}
