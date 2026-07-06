package com.pd.labs.cattlebook.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.pd.labs.cattlebook.data.db.dao.FeedDao
import com.pd.labs.cattlebook.data.db.dao.MilkDao
import com.pd.labs.cattlebook.data.db.dao.SettlementPeriodDao
import com.pd.labs.cattlebook.data.db.entity.FeedEntry
import com.pd.labs.cattlebook.data.db.entity.MilkEntry
import com.pd.labs.cattlebook.data.db.entity.MilkSession
import com.pd.labs.cattlebook.data.db.entity.SettlementPeriod

class Converters {
    @TypeConverter fun fromSession(s: MilkSession): String = s.name
    @TypeConverter fun toSession(s: String): MilkSession = MilkSession.valueOf(s)
}

@Database(
    entities = [MilkEntry::class, FeedEntry::class, SettlementPeriod::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CattleBookDatabase : RoomDatabase() {
    abstract fun milkDao(): MilkDao
    abstract fun feedDao(): FeedDao
    abstract fun settlementPeriodDao(): SettlementPeriodDao

    companion object {
        @Volatile private var INSTANCE: CattleBookDatabase? = null

        private const val DB_NAME = "cattlebook.db"

        fun getInstance(context: Context): CattleBookDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, CattleBookDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
