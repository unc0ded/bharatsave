package com.bharatsave.goldapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "plans_table")
@JsonClass(generateAdapter = true)
data class PlanDetail(
    @Json(name = "subscriptionId")
    val id: String,
    @PrimaryKey
    val planName: String,
    val active: Boolean,
    val userId: String
)
