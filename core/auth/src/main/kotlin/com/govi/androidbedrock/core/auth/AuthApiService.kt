package com.govi.androidbedrock.core.auth

import com.govi.androidbedrock.core.utils.AuthConstants
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

/**
 * API service for authentication and core identity tasks
 */
interface AuthApiService {

    @POST(AuthConstants.PATH_LOGIN)
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST(AuthConstants.PATH_REFRESH)
    suspend fun refreshToken(@Body request: RefreshTokenRequest): AuthResponse

    @GET(AuthConstants.PATH_HEALTH)
    suspend fun checkHealth(): HealthResponse
}

// Request/Response DTOs
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val tokenType: String = "Bearer"
)

@Serializable
data class HealthResponse(
    val status: String,
    val message: String? = null
)
