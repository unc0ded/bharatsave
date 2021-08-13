package com.bharatsave.goldapp.model

data class GoalDetails(
    val goalName: String,
    val goalTarget: Float? = null,
    val amountInvested: Float? = null,
    val currentValue: Float,
    val goldWeight: Float = 5.255f
)