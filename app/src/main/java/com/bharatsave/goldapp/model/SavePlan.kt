package com.bharatsave.goldapp.model

enum class SavePlan(private val planName: String) {
    ROUND_UP("Round Up"), DAILY_SAVINGS("Daily Savings"), WEEKLY_SAVINGS("Weekly Savings"), MONTHLY_SAVINGS(
        "Monthly Savings"
    );

    override fun toString(): String {
        return planName
    }
}