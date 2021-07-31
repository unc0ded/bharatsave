package com.dev.`in`.drogon.data.db

import androidx.room.*
import com.dev.`in`.drogon.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    suspend fun getUser(): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE from user_table")
    suspend fun deleteUser()
}