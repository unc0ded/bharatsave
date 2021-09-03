package com.bharatsave.goldapp.data.service

import com.bharatsave.goldapp.model.AuthResponse
import com.bharatsave.goldapp.model.BalanceDetail
import com.bharatsave.goldapp.model.BankDetail
import com.bharatsave.goldapp.model.augmont.GoldRate
import com.bharatsave.goldapp.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AugmontService {

    @POST("augmont/createuser")
    suspend fun signUp(@Body user: User): AuthResponse

    @POST("augmont/login")
    suspend fun login(@Body map: Map<String, String>): AuthResponse

    @GET("augmont/goldrate")
    suspend fun goldRate(): GoldRate

    @POST("augmont/buy")
    suspend fun purchaseGold(@Body map: Map<String, String>): BalanceDetail

    @POST("augmont/sell")
    suspend fun sellGold(@Body map: Map<String, String>): BalanceDetail

    @POST("augmont/userbankcreate")
    suspend fun createBank(@Body map: Map<String, String>): BankDetail
}