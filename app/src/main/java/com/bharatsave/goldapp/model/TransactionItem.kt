package com.bharatsave.goldapp.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(tableName = "transactions_list")
@JsonClass(generateAdapter = true)
data class TransactionItem(
    val quantity: String,
    val totalAmount: String?,
    val metalType: String,
    val transactionId: String?,
    @PrimaryKey
    val merchantTransactionId: String,
    val invoiceNumber: String?,
    val productName: String?,
    val userName: String?,
    @Json(name = "uniqueId")
    val userId: String,
    val rate: String?,
    val orderId: String?,
    val shippingAddressId: String?,
    val shippingCharges: String?,
    val bankId: String?,
    val txnType: TransactionType,
    val createdAt: String,
    val updatedAt: String
)
