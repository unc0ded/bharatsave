package com.bharatsave.goldapp.model

import androidx.room.Embedded
import androidx.room.Relation

data class UserPlan(
    @Embedded val user: User,
    @Relation(parentColumn = "id", entityColumn = "userId")
    val activePlans: List<PlanDetail>
)
