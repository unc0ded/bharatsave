package com.dev.`in`.drogon.data.service

import com.dev.`in`.drogon.model.AuthResponse
import com.dev.`in`.drogon.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/signup")
    suspend fun signUp(@Body user: User): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body map: Map<String, String>): AuthResponse
}