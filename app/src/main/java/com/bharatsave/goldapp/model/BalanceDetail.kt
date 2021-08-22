package com.bharatsave.goldapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BalanceDetail(
    @Json(name = "totalAmount")
    val amountInvested: String = "0",
    val goldBalance: String = "0"
)