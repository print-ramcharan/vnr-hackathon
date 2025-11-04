package com.codewithram.medvault.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val username: String?,
    val role: String?,
    val message: String?,
    val first_login: Boolean?,
    val isProfileComplete: Boolean?
)

interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<AuthResponse>
}
