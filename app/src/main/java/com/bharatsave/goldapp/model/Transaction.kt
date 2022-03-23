package com.bharatsave.goldapp.model

import java.time.LocalDate
import java.time.LocalDateTime

sealed class Transaction {
    class TransactionDetail(
        val dateTime: LocalDateTime,
        val amount: String? = null,
        val goldWeight: String,
        val description: String? = null,
        val type: TransactionType
    ) : Transaction()

    class TransactionHeader(val date: LocalDate) : Transaction()
}