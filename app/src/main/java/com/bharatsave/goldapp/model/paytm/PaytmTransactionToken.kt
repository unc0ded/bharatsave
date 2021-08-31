package com.bharatsave.goldapp.model.paytm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaytmTransactionToken(
    val orderId: String,
    @Json(name = "mid")
    val merchantId: String,
    val amount: String,
    @Json(name = "token")
    val transactionToken: String
)