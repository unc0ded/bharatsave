package com.bharatsave.goldapp.data.service

import com.bharatsave.goldapp.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserService {

    @GET("user/")
    suspend fun userDetails(): User

    @PUT("user/")
    suspend fun updateDetails(@Body user: User): User

    @GET("user/balance")
    suspend fun balanceDetails(): BalanceDetail

    @GET("user/transactions")
    suspend fun userTransactions(): List<TransactionItem>

    @GET("user/bankDetails")
    suspend fun userBankDetails(): List<BankDetail>

    @GET("user/addresses")
    suspend fun userAddresses(): List<AddressDetail>
}