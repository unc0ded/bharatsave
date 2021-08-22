package com.bharatsave.goldapp.data.db

import androidx.room.*
import com.bharatsave.goldapp.model.BalanceDetail
import com.bharatsave.goldapp.model.BankDetail
import com.bharatsave.goldapp.model.User
import com.bharatsave.goldapp.model.UserBank
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT amountInvested, goldBalance FROM user_table")
    fun getBalanceDetails(): Flow<BalanceDetail>

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE user_table SET amountInvested = :totalAmount, goldBalance = :goldBalance WHERE phoneNumber = :phoneNumber")
    suspend fun updateBalanceDetails(totalAmount: String, goldBalance: String, phoneNumber: String)

    @Query("DELETE from user_table")
    suspend fun deleteUser()
}