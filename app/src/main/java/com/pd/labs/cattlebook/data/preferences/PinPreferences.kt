package com.pd.labs.cattlebook.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("cattlebook_prefs")

class PinPreferences(private val context: Context) {
    companion object {
        private val PIN_KEY = stringPreferencesKey("pin")
        private val PIN_ENABLED_KEY = booleanPreferencesKey("pin_enabled")
    }

    val pinEnabled = context.dataStore.data.map { it[PIN_ENABLED_KEY] ?: false }
    val pin = context.dataStore.data.map { it[PIN_KEY] ?: "" }

    suspend fun setPin(pin: String) {
        context.dataStore.edit {
            it[PIN_KEY] = pin
            it[PIN_ENABLED_KEY] = true
        }
    }

    suspend fun disablePin() {
        context.dataStore.edit {
            it[PIN_ENABLED_KEY] = false
            it[PIN_KEY] = ""
        }
    }
}
