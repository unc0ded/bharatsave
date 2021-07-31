package com.dev.`in`.drogon.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.`in`.drogon.model.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}