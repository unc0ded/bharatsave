package com.bharatsave.goldapp.model.augmont

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import java.time.Instant
import java.time.LocalDateTime

@Entity(tableName = "gold_rates")
@JsonClass(generateAdapter = true)
data class GoldRate(
    val blockId: String,
    val buyPrice: String,
    val buyGst: String,
    val sellPrice: String,
    @PrimaryKey
    val timeStamp: Long = Instant.now().toEpochMilli()
)