package io.github.praveendeviam.cattlebook

import android.app.Application
import android.content.Context
import io.github.praveendeviam.cattlebook.data.db.CattleBookDatabase
import io.github.praveendeviam.cattlebook.data.preferences.PinPreferences
import io.github.praveendeviam.cattlebook.data.repository.LedgerRepository

class CattleBookApp : Application() {
    val database by lazy { CattleBookDatabase.getInstance(this) }
    val repository by lazy {
        LedgerRepository(database.milkDao(), database.feedDao(), database.settlementPeriodDao())
    }
    val pinPreferences by lazy { PinPreferences(this) }
}

val Context.app get() = applicationContext as CattleBookApp
