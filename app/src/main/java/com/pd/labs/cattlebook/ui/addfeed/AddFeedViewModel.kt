package com.pd.labs.cattlebook.ui.addfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pd.labs.cattlebook.data.db.entity.FeedEntry
import com.pd.labs.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AddFeedState(
    val date: LocalDate = LocalDate.now(),
    val item: String = "Cattle feed",
    val bags: String = "",
    val pricePerBag: String = "",
    val amount: String = "",
    val note: String = "",
    val saving: Boolean = false,
    val isEditMode: Boolean = false,
)

class AddFeedViewModel(
    private val repository: LedgerRepository,
    private val entryId: Long? = null
) : ViewModel() {

    private val _state = MutableStateFlow(AddFeedState())
    val state: StateFlow<AddFeedState> = _state.asStateFlow()

    init {
        if (entryId != null) {
            viewModelScope.launch {
                val entry = repository.getFeedById(entryId) ?: return@launch
                _state.update {
                    it.copy(
                        date = LocalDate.ofEpochDay(entry.date),
                        item = entry.item,
                        bags = entry.bags.toString(),
                        pricePerBag = entry.pricePerBag?.toString() ?: "",
                        amount = entry.amount.toString(),
                        note = entry.note ?: "",
                        isEditMode = true
                    )
                }
            }
        }
    }

    fun onDateChange(d: LocalDate) = _state.update { it.copy(date = d) }
    fun onItemChange(v: String) = _state.update { it.copy(item = v) }
    fun onBagsChange(v: String) {
        _state.update { it.copy(bags = v) }
        recalcAmount()
    }
    fun onPriceChange(v: String) {
        _state.update { it.copy(pricePerBag = v) }
        recalcAmount()
    }
    fun onAmountChange(v: String) = _state.update { it.copy(amount = v) }
    fun onNoteChange(v: String) = _state.update { it.copy(note = v) }

    private fun recalcAmount() {
        val s = _state.value
        val b = s.bags.toIntOrNull() ?: return
        val p = s.pricePerBag.toDoubleOrNull() ?: return
        _state.update { it.copy(amount = "%.2f".format(b * p)) }
    }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        val bags = s.bags.toIntOrNull() ?: return
        val amount = s.amount.toDoubleOrNull() ?: return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            if (entryId != null) {
                val existing = repository.getFeedById(entryId) ?: return@launch
                repository.updateFeedEntry(
                    existing.copy(
                        date = s.date.toEpochDay(),
                        item = s.item.ifBlank { "Cattle feed" },
                        bags = bags,
                        pricePerBag = s.pricePerBag.toDoubleOrNull(),
                        amount = amount,
                        note = s.note.ifBlank { null }
                    )
                )
            } else {
                val periodId = repository.ensureCurrentPeriod()
                repository.addFeedEntry(
                    FeedEntry(
                        date = s.date.toEpochDay(),
                        item = s.item.ifBlank { "Cattle feed" },
                        bags = bags,
                        pricePerBag = s.pricePerBag.toDoubleOrNull(),
                        amount = amount,
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
                initializer { AddFeedViewModel(repository, entryId) }
            }
    }
}
