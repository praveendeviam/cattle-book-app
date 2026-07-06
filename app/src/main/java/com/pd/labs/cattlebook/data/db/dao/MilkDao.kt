package com.pd.labs.cattlebook.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pd.labs.cattlebook.data.db.entity.MilkEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MilkDao {
    @Insert
    suspend fun insert(entry: MilkEntry): Long

    @Update
    suspend fun update(entry: MilkEntry)

    @Delete
    suspend fun delete(entry: MilkEntry)

    @Query("SELECT * FROM milk_entries WHERE id = :id")
    suspend fun getById(id: Long): MilkEntry?

    @Query("SELECT * FROM milk_entries WHERE periodId = :periodId ORDER BY date DESC, session DESC")
    fun getForPeriod(periodId: Long): Flow<List<MilkEntry>>

    @Query("SELECT * FROM milk_entries ORDER BY date DESC, session DESC")
    fun getAll(): Flow<List<MilkEntry>>

    @Query("SELECT COALESCE(SUM(litres), 0) FROM milk_entries WHERE periodId = :periodId")
    fun getTotalLitresForPeriod(periodId: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(litres), 0) FROM milk_entries WHERE date >= :startDay AND date <= :endDay")
    suspend fun getTotalLitresInRange(startDay: Long, endDay: Long): Double
}
