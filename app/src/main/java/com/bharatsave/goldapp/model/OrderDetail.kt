package com.bharatsave.goldapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderDetail(
    @Json(name = "uniqueId")
    val id: String,
    @Json(name = "merchantTransactionId")
    val merchantTxnId: String,
    val orderId: String,
    val shippingCharges: String,
    val productName: String,
    @Json(name = "shippingAddressId")
    val addressId: String
)
