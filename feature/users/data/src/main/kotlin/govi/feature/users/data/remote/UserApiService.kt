package govi.feature.users.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApiService {
    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: String,
    ): Response<UserDto>
}
