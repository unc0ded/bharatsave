package com.bharatsave.goldapp.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserBank(
    @Embedded val user: User,
    @Relation(parentColumn = "id", entityColumn = "userId")
    val userBanks: List<BankDetail>
)
