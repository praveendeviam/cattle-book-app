package com.pd.labs.cattlebook.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settlement_periods")
data class SettlementPeriod(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDate: Long,
    val endDate: Long? = null,
    val paymentAmount: Double? = null,
    val paymentDate: Long? = null,
    val isClosed: Boolean = false
)
