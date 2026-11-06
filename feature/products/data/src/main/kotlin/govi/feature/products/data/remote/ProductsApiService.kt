package govi.feature.products.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductsApiService {
    @GET("products")
    suspend fun getItems(): Response<List<ProductsDto>>

    @GET("products/{id}")
    suspend fun getItemById(@Path("id") id: String): Response<ProductsDto>
}
