package io.github.praveendeviam.cattlebook.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.praveendeviam.cattlebook.data.db.entity.FeedEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Insert
    suspend fun insert(entry: FeedEntry): Long

    @Update
    suspend fun update(entry: FeedEntry)

    @Delete
    suspend fun delete(entry: FeedEntry)

    @Query("SELECT * FROM feed_entries WHERE id = :id")
    suspend fun getById(id: Long): FeedEntry?

    @Query("SELECT * FROM feed_entries WHERE periodId = :periodId ORDER BY date DESC")
    fun getForPeriod(periodId: Long): Flow<List<FeedEntry>>

    @Query("SELECT * FROM feed_entries ORDER BY date DESC")
    fun getAll(): Flow<List<FeedEntry>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM feed_entries WHERE periodId = :periodId")
    fun getTotalAmountForPeriod(periodId: Long): Flow<Double>
}
