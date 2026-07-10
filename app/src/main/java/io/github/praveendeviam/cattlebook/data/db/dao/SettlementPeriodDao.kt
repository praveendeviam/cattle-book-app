package io.github.praveendeviam.cattlebook.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.praveendeviam.cattlebook.data.db.entity.SettlementPeriod
import kotlinx.coroutines.flow.Flow

@Dao
interface SettlementPeriodDao {
    @Insert
    suspend fun insert(period: SettlementPeriod): Long

    @Update
    suspend fun update(period: SettlementPeriod)

    @Query("SELECT * FROM settlement_periods WHERE isClosed = 0 LIMIT 1")
    fun getCurrentPeriod(): Flow<SettlementPeriod?>

    @Query("SELECT * FROM settlement_periods WHERE isClosed = 0 LIMIT 1")
    suspend fun getCurrentPeriodOnce(): SettlementPeriod?

    @Query("SELECT * FROM settlement_periods ORDER BY startDate DESC")
    fun getAll(): Flow<List<SettlementPeriod>>
}
