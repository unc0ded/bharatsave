package com.bharatsave.goldapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BuyTransactionListItem(

    val cancelId: String?,
    val createdAt: String,
    val discountAmt: String?,
    val exclTaxAmt: Double,
    val exclTaxRate: Double,
    val inclTaxAmt: Float,
    val inclTaxRate: Float,
    val merchantTransactionId: String,
    val modeOfPayment: String?,
    val qty: Float,
    val taxAmt: String,
    val taxRate: Double,
    val transactionId: String,
    val type: String,
    val uniqueId: String
)