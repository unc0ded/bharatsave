package com.bharatsave.goldapp.data.db

import androidx.room.*
import com.bharatsave.goldapp.model.GoldRate
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface MainDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertGoldRate(goldRate: GoldRate)

    @Query("SELECT * FROM gold_rates ORDER BY timeStamp DESC LIMIT 1")
    suspend fun getLastGoldRate(): GoldRate

    @Query("SELECT * FROM gold_rates ORDER BY timeStamp DESC LIMIT 2")
    fun getGoldRates(): Flow<List<GoldRate>>

    @Query("SELECT COUNT(*) FROM gold_rates")
    suspend fun getGoldRateEntryCount(): Int

    @Query("DELETE FROM gold_rates WHERE timeStamp NOT IN (SELECT timeStamp from gold_rates ORDER BY timeStamp DESC LIMIT 10)")
    suspend fun deleteOldRates()
}