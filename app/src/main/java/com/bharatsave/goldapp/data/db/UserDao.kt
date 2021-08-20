package com.bharatsave.goldapp.data.db

import androidx.room.*
import com.bharatsave.goldapp.model.BankDetail
import com.bharatsave.goldapp.model.User
import com.bharatsave.goldapp.model.UserBank

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    suspend fun getUser(): User

    @Transaction
    @Query("SELECT * FROM user_table")
    suspend fun getUserWithBanks(): UserBank

    @Query("SELECT * FROM banks_list")
    suspend fun getUserBanks(): List<BankDetail>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("DELETE from user_table")
    suspend fun deleteUser()
}