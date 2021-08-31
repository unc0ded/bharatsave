package com.bharatsave.goldapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bharatsave.goldapp.model.BankDetail
import com.bharatsave.goldapp.model.PaytmTransaction
import com.bharatsave.goldapp.model.PlanDetail
import com.bharatsave.goldapp.model.augmont.GoldRate
import com.bharatsave.goldapp.model.User

@Database(
    entities = [User::class, BankDetail::class, GoldRate::class, PlanDetail::class, PaytmTransaction::class],
    version = 5,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mainDao(): MainDao
}