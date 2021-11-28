package com.bharatsave.goldapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderResponse(
    val orderDetails: OrderDetail,
    val goldBalance: String,
    val message: String
)
