package com.pd.labs.cattlebook

import android.app.Application
import android.content.Context
import com.pd.labs.cattlebook.data.db.CattleBookDatabase
import com.pd.labs.cattlebook.data.preferences.PinPreferences
import com.pd.labs.cattlebook.data.repository.LedgerRepository

class CattleBookApp : Application() {
    val database by lazy { CattleBookDatabase.getInstance(this) }
    val repository by lazy {
        LedgerRepository(database.milkDao(), database.feedDao(), database.settlementPeriodDao())
    }
    val pinPreferences by lazy { PinPreferences(this) }
}

val Context.app get() = applicationContext as CattleBookApp
