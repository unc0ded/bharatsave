package com.bharatsave.goldapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "message")
    val message: String,
    @Json(name = "auth_token")
    val authToken: String,
    @Json(name = "refresh_token")
    val refreshToken: String,
    @Json(name = "user")
    val user: User?
)