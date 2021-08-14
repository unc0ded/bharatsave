package com.bharatsave.goldapp.model

import java.time.LocalDate
import java.time.LocalDateTime

sealed class Transaction {
    class TransactionItem(
        val dateTime: LocalDateTime,
        val amount: Float,
        val goldWorth: Float,
        val plan: SavePlan
    ) : Transaction()

    class TransactionHeader(val date: LocalDate) : Transaction()
}