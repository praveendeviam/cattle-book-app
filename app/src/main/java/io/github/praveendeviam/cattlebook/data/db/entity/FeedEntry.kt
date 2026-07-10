package io.github.praveendeviam.cattlebook.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_entries")
data class FeedEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val item: String,
    val bags: Int,
    val pricePerBag: Double?,
    val amount: Double,
    val note: String?,
    val periodId: Long
)
