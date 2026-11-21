package com.govi.androidbedrock.feature.demo.domain.repository

import com.govi.androidbedrock.feature.demo.domain.model.DemoItem

interface DemoRepository {
    suspend fun getItems(): Result<List<DemoItem>>

    suspend fun getItemById(id: String): Result<DemoItem>
}
