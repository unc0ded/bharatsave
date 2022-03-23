package com.bharatsave.goldapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BalanceDetail(
    val goldBalance: String = "0"
)