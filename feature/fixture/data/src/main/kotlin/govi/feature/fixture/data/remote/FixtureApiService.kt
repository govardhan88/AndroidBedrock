package govi.feature.fixture.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FixtureApiService {
    @GET("fixture")
    suspend fun getItems(): Response<List<FixtureDto>>

    @GET("fixture/{id}")
    suspend fun getItemById(@Path("id") id: String): Response<FixtureDto>
}
