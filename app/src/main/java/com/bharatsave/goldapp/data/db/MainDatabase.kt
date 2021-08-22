package com.bharatsave.goldapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bharatsave.goldapp.model.BankDetail
import com.bharatsave.goldapp.model.GoldRate
import com.bharatsave.goldapp.model.User

@Database(entities = [User::class, BankDetail::class, GoldRate::class], version = 3, exportSchema = false)
abstract class MainDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun mainDao(): MainDao
}