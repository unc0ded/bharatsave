package com.bharatsave.goldapp.data.service

import com.bharatsave.goldapp.model.*
import com.bharatsave.goldapp.model.augmont.GoldRate
import retrofit2.http.*

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

    @POST("augmont/bank")
    suspend fun createBank(@Body map: Map<String, String>): BankDetail

    /*
    Expected body parameters: accountNumber, accountName, ifscCode
     */
    @PUT("augmont/bank")
    suspend fun updateBank(
        @Query("userBankId") bankId: String,
        @Body map: Map<String, String>
    ): BankDetail

    @DELETE("augmont/bank")
    suspend fun deleteBank(@Query("userBankId") bankId: String)

    @GET("augmont/products")
    suspend fun goldProducts(): List<GoldCoin>

    @POST("augmont/address")
    suspend fun createAddress(@Body map: Map<String, String>): AddressDetail

    @POST("augmont/order")
    suspend fun orderProduct(@Body map: Map<String, String>): OrderResponse

    @GET("augmont/buylist")
    suspend fun getBuyList(): List<BuyTransactionListItem>

    @GET("augmont/selllist")
    suspend fun getSellList(): List<SellTransactionListItem>
}