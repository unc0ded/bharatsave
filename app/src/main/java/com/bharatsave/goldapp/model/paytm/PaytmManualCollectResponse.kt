package com.bharatsave.goldapp.model.paytm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaytmManualCollectResponse(
    val orderId: String,
    val subscriptionId: String,
    @Json(name = "txnId")
    val transactionId: String,
    val message: String
)
