package com.bharatsave.goldapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bharatsave.goldapp.model.BankDetail
import com.bharatsave.goldapp.model.User

@Database(entities = [User::class, BankDetail::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}