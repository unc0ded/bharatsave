package com.bharatsave.goldapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BuyTransactionListItem(
    val createdAt: String,
    val exclTaxAmt: Float,
    val exclTaxRate: Float,
    val inclTaxAmt: Float,
    val inclTaxRate: Float,
    val merchantTransactionId: String,
    val qty: Float,
    val taxAmt: String,
    val taxRate: Float,
    val transactionId: String,
    val type: String,
    val uniqueId: String
)