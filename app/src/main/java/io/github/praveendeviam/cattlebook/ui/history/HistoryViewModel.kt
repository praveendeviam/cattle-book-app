package io.github.praveendeviam.cattlebook.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.praveendeviam.cattlebook.data.db.entity.MilkEntry
import io.github.praveendeviam.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class HistoryUiState(
    val month: YearMonth = YearMonth.now(),
    val allEntries: List<MilkEntry> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
)

class HistoryViewModel(private val repository: LedgerRepository) : ViewModel() {

    private val _month = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<HistoryUiState> = combine(
        _month,
        _selectedDate,
        repository.getAllMilk()
    ) { month, selectedDate, entries ->
        HistoryUiState(month = month, allEntries = entries, selectedDate = selectedDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun prevMonth() { _month.update { it.minusMonths(1) } }
    fun nextMonth() { _month.update { it.plusMonths(1) } }
    fun selectDate(date: LocalDate) { _selectedDate.value = date }
    fun deleteMilk(entry: MilkEntry) = viewModelScope.launch { repository.deleteMilkEntry(entry) }

    companion object {
        fun factory(repository: LedgerRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HistoryViewModel(repository) }
        }
    }
}
