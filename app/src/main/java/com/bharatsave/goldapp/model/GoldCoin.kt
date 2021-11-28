package com.bharatsave.goldapp.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoldCoin(
    val name: String,
    val sku: String,
    val productWeight: String,
    val makingCharges: String
) {
    override fun toString() = name.substringBefore(" (")
}
