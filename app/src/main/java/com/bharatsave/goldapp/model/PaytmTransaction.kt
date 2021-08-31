package com.bharatsave.goldapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_list")
data class PaytmTransaction(
    @PrimaryKey
    val orderId: String,
    val transactionId: String,
    val bankTransactionId: String,
    val amount: String,
    val timestamp: Long,
    val plan: String = ""
)