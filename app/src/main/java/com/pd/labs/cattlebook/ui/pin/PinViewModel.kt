package com.pd.labs.cattlebook.ui.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pd.labs.cattlebook.data.preferences.PinPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PinState(
    val digits: String = "",
    val error: Boolean = false,
)

class PinViewModel(private val prefs: PinPreferences) : ViewModel() {
    private val _state = MutableStateFlow(PinState())
    val state: StateFlow<PinState> = _state.asStateFlow()

    val pinEnabled = prefs.pinEnabled

    fun onDigit(d: String) {
        val current = _state.value.digits
        if (current.length < 4) _state.update { it.copy(digits = current + d, error = false) }
    }

    fun onDelete() {
        val current = _state.value.digits
        if (current.isNotEmpty()) _state.update { it.copy(digits = current.dropLast(1), error = false) }
    }

    fun reset() = _state.update { PinState() }

    fun verify(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val saved = prefs.pin.first()
            if (_state.value.digits == saved) {
                onSuccess()
            } else {
                _state.update { it.copy(digits = "", error = true) }
            }
        }
    }

    fun setupPin(pin: String, onDone: () -> Unit) {
        viewModelScope.launch {
            prefs.setPin(pin)
            onDone()
        }
    }

    fun disablePin(onDone: () -> Unit) {
        viewModelScope.launch {
            prefs.disablePin()
            onDone()
        }
    }

    companion object {
        fun factory(prefs: PinPreferences): ViewModelProvider.Factory = viewModelFactory {
            initializer { PinViewModel(prefs) }
        }
    }
}
