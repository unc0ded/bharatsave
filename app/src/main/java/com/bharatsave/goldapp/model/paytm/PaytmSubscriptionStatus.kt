package com.bharatsave.goldapp.model.paytm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaytmSubscriptionStatus(
    val subscriptionId: String,
    val status: String,
    val orderId: String,
    val frequencyUnit: String,
    val frequency: String,
    @Json(name = "expiryDate")
    val endDate: String,
    @Json(name = "custId")
    val userId: String,
    val lastOrderId: String,
    val lastOrderStatus: String,
    val lastOrderCreationDate: String,
    val lastOrderAmount: String
)
