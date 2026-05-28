package com.govi.androidbedrock.demo.data

import retrofit2.http.GET

interface DemoApiService {
    @GET("demo/items")
    suspend fun getDemoItems(): List<DemoItem>
}
