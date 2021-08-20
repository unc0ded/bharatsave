package com.bharatsave.goldapp.data.service

import com.bharatsave.goldapp.model.AuthResponse
import com.bharatsave.goldapp.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AugmontService {

    @POST("augmont/createuser")
    suspend fun signUp(@Body user: User): AuthResponse

    @POST("augmont/login")
    suspend fun login(@Body map: Map<String, String>): AuthResponse
}