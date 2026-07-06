package com.pd.labs.cattlebook.data.repository

import com.pd.labs.cattlebook.data.db.dao.FeedDao
import com.pd.labs.cattlebook.data.db.dao.MilkDao
import com.pd.labs.cattlebook.data.db.dao.SettlementPeriodDao
import com.pd.labs.cattlebook.data.db.entity.FeedEntry
import com.pd.labs.cattlebook.data.db.entity.MilkEntry
import com.pd.labs.cattlebook.data.db.entity.SettlementPeriod
import java.time.LocalDate

class LedgerRepository(
    private val milkDao: MilkDao,
    private val feedDao: FeedDao,
    private val periodDao: SettlementPeriodDao
) {
    fun getCurrentPeriod() = periodDao.getCurrentPeriod()
    fun getAllPeriods() = periodDao.getAll()
    fun getAllMilk() = milkDao.getAll()
    fun getAllFeed() = feedDao.getAll()
    fun getMilkForPeriod(id: Long) = milkDao.getForPeriod(id)
    fun getFeedForPeriod(id: Long) = feedDao.getForPeriod(id)
    fun getTotalLitres(id: Long) = milkDao.getTotalLitresForPeriod(id)
    fun getFeedTotal(id: Long) = feedDao.getTotalAmountForPeriod(id)

    suspend fun getTotalLitresInRange(startDay: Long, endDay: Long) =
        milkDao.getTotalLitresInRange(startDay, endDay)

    suspend fun getMilkById(id: Long) = milkDao.getById(id)
    suspend fun getFeedById(id: Long) = feedDao.getById(id)

    suspend fun ensureCurrentPeriod(): Long {
        val existing = periodDao.getCurrentPeriodOnce()
        if (existing != null) return existing.id
        return periodDao.insert(SettlementPeriod(startDate = LocalDate.now().toEpochDay()))
    }

    suspend fun addMilkEntry(entry: MilkEntry) = milkDao.insert(entry)
    suspend fun addFeedEntry(entry: FeedEntry) = feedDao.insert(entry)
    suspend fun updateMilkEntry(entry: MilkEntry) = milkDao.update(entry)
    suspend fun updateFeedEntry(entry: FeedEntry) = feedDao.update(entry)
    suspend fun deleteMilkEntry(entry: MilkEntry) = milkDao.delete(entry)
    suspend fun deleteFeedEntry(entry: FeedEntry) = feedDao.delete(entry)

    suspend fun recordPayment(amount: Double, date: LocalDate) {
        val current = periodDao.getCurrentPeriodOnce() ?: return
        periodDao.update(
            current.copy(
                endDate = date.toEpochDay(),
                paymentAmount = amount,
                paymentDate = date.toEpochDay(),
                isClosed = true
            )
        )
        periodDao.insert(SettlementPeriod(startDate = LocalDate.now().toEpochDay()))
    }
}
