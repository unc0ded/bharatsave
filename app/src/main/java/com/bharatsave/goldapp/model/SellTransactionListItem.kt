package com.bharatsave.goldapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SellTransactionListItem(
    val amount: Float,
    val createdAt: String,
    val merchantTransactionId: String,
    val qty: Float,
    val rate: Float,
    val transactionId: String,
    val type: String
)