package com.bharatsave.goldapp.model

import androidx.room.Entity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "banks_list", primaryKeys = ["userId", "accountNo"])
@JsonClass(generateAdapter = true)
data class BankDetail(
    @Json(name = "userBankId")
    val id: String,
    val ifscCode: String,
    @Json(name = "accountNumber")
    val accountNo: String,
    val accountName: String,
    val bankName: String,
    @Json(name = "uniqueId")
    val userId: String
)