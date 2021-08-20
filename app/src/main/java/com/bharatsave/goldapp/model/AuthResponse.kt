package com.bharatsave.goldapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "token")
    val authToken: String,
//    @Json(name = "refresh_token")
//    val refreshToken: String,
    @Json(name = "userData")
    val user: User?
)