package govi.feature.settings.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SettingsApiService {
    @GET("settings")
    suspend fun getItems(): Response<List<SettingsDto>>

    @GET("settings/{id}")
    suspend fun getItemById(
        @Path("id") id: String,
    ): Response<SettingsDto>
}
