package com.bharatsave.goldapp.model.paytm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaytmTransactionStatus(
    val orderId: String,
    @Json(name = "txnId")
    val transactionId: String,
    @Json(name = "bankTxnId")
    val bankTransactionId: String,
    @Json(name = "txnAmount")
    val transactionAmount: String,
    @Json(name = "txnDate")
    val date: String,
    val status: String,
    val message: String
)
