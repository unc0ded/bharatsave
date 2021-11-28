package com.bharatsave.goldapp.data.db

import androidx.room.*
import androidx.room.Transaction
import com.bharatsave.goldapp.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_table")
    suspend fun getUser(): User

    @Query("SELECT amountInvested, goldBalance FROM user_table")
    fun getBalanceDetails(): Flow<BalanceDetail>

    @Transaction
    @Query("SELECT * FROM user_table")
    suspend fun getUserWithBanks(): UserWithBanks

    @Query("SELECT * FROM banks_list")
    suspend fun getUserBanks(): List<BankDetail>

    @Query("SELECT * FROM address_list")
    suspend fun getAddresses(): List<AddressDetail>

    @Transaction
    @Query("SELECT * FROM user_table")
    suspend fun getUserWithPlans(): UserWithPlans

    @Query("SELECT * FROM plans_table")
    suspend fun getUserPlans(): List<PlanDetail>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserPlan(planDetail: PlanDetail)

    @Insert
    suspend fun saveTransaction(transaction: PaytmTransaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanks(vararg bankDetails: BankDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(vararg addresses: AddressDetail)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE user_table SET amountInvested = :totalAmount, goldBalance = :goldBalance WHERE phoneNumber = :phoneNumber")
    suspend fun updateBalanceDetails(totalAmount: String, goldBalance: String, phoneNumber: String)

    @Query("UPDATE user_table SET goldBalance = :goldBalance WHERE id = :userId")
    suspend fun updateGoldBalance(goldBalance: String, userId: String)

    @Query("DELETE from user_table")
    suspend fun deleteUser()
}