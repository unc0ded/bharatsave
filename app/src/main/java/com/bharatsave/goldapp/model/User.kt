package com.bharatsave.goldapp.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "user_table")
@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "_id")
    val id: String?,
    @Json(name = "userName")
    val name: String,
    @Json(name = "emailId")
    val email: String,
    @PrimaryKey
    @Json(name = "mobileNumber")
    val phoneNumber: String,
    @Json(name = "userPincode")
    val pinCode: String = "",
    @Embedded
    val balanceDetail: BalanceDetail = BalanceDetail()
)