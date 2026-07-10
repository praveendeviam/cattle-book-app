package io.github.praveendeviam.cattlebook.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MilkSession { MORNING, EVENING }

@Entity(tableName = "milk_entries")
data class MilkEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val session: MilkSession,
    val litres: Double,
    val note: String?,
    val periodId: Long
)
