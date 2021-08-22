package com.bharatsave.goldapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import java.time.Instant
import java.time.LocalDateTime

@Entity(tableName = "gold_rates")
@JsonClass(generateAdapter = true)
data class GoldRate(
    val blockId: String,
    val goldPrice: String,
    val tax: String,
    val totalBuyPrice: String,
    val totalSellPrice: String,
    @PrimaryKey
    val timeStamp: Long = Instant.now().toEpochMilli()
)