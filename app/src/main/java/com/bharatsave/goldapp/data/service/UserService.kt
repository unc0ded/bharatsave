package com.bharatsave.goldapp.data.service

import com.bharatsave.goldapp.model.BalanceDetail
import com.bharatsave.goldapp.model.BankDetail
import com.bharatsave.goldapp.model.User
import okhttp3.ResponseBody
import retrofit2.http.GET

interface UserService {

    @GET("user/")
    suspend fun userDetails(): User

    @GET("user/balance")
    suspend fun balanceDetails(): BalanceDetail

    @GET("user/bankDetails")
    suspend fun userBankDetails(): List<BankDetail>
}