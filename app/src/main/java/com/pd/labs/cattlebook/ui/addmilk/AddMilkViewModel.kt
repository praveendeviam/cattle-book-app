package com.pd.labs.cattlebook.ui.addmilk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pd.labs.cattlebook.data.db.entity.MilkEntry
import com.pd.labs.cattlebook.data.db.entity.MilkSession
import com.pd.labs.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddMilkState(
    val date: LocalDate = LocalDate.now(),
    val session: MilkSession = MilkSession.MORNING,
    val litres: String = "",
    val note: String = "",
    val saving: Boolean = false,
    val isEditMode: Boolean = false,
)

class AddMilkViewModel(
    private val repository: LedgerRepository,
    private val entryId: Long? = null
) : ViewModel() {

    private val _state = MutableStateFlow(AddMilkState())
    val state: StateFlow<AddMilkState> = _state.asStateFlow()

    init {
        if (entryId != null) {
            viewModelScope.launch {
                val entry = repository.getMilkById(entryId) ?: return@launch
                _state.update {
                    it.copy(
                        date = LocalDate.ofEpochDay(entry.date),
                        session = entry.session,
                        litres = entry.litres.toString(),
                        note = entry.note ?: "",
                        isEditMode = true
                    )
                }
            }
        }
    }

    fun onDateChange(d: LocalDate) = _state.update { it.copy(date = d) }
    fun onSessionChange(s: MilkSession) = _state.update { it.copy(session = s) }
    fun onLitresChange(v: String) = _state.update { it.copy(litres = v) }
    fun onNoteChange(v: String) = _state.update { it.copy(note = v) }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        val litres = s.litres.toDoubleOrNull() ?: return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            if (entryId != null) {
                val existing = repository.getMilkById(entryId) ?: return@launch
                repository.updateMilkEntry(
                    existing.copy(
                        date = s.date.toEpochDay(),
                        session = s.session,
                        litres = litres,
                        note = s.note.ifBlank { null }
                    )
                )
            } else {
                val periodId = repository.ensureCurrentPeriod()
                repository.addMilkEntry(
                    MilkEntry(
                        date = s.date.toEpochDay(),
                        session = s.session,
                        litres = litres,
                        note = s.note.ifBlank { null },
                        periodId = periodId
                    )
                )
            }
            onDone()
        }
    }

    companion object {
        fun factory(repository: LedgerRepository, entryId: Long? = null): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { AddMilkViewModel(repository, entryId) }
            }
    }
}
