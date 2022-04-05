package com.bharatsave.goldapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bharatsave.goldapp.model.*
import com.bharatsave.goldapp.model.augmont.GoldRate

@Database(
    entities = [User::class, BankDetail::class, GoldRate::class, PlanDetail::class, TransactionItem::class, AddressDetail::class],
    version = 9,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mainDao(): MainDao
}