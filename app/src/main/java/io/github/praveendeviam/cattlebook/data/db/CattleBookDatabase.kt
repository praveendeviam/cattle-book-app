package io.github.praveendeviam.cattlebook.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import io.github.praveendeviam.cattlebook.data.db.dao.FeedDao
import io.github.praveendeviam.cattlebook.data.db.dao.MilkDao
import io.github.praveendeviam.cattlebook.data.db.dao.SettlementPeriodDao
import io.github.praveendeviam.cattlebook.data.db.entity.FeedEntry
import io.github.praveendeviam.cattlebook.data.db.entity.MilkEntry
import io.github.praveendeviam.cattlebook.data.db.entity.MilkSession
import io.github.praveendeviam.cattlebook.data.db.entity.SettlementPeriod

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
