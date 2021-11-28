package com.bharatsave.goldapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "address_list")
@JsonClass(generateAdapter = true)
data class AddressDetail(
    @PrimaryKey
    @Json(name = "addressId")
    val id: String,
    @Json(name = "addressType")
    val label: String,
    val address: String,
    val city: String,
    val state: String,
    val pincode: String,
    val mobileNumber: String,
    val name: String
)
