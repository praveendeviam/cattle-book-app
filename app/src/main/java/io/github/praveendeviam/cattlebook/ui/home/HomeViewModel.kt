package io.github.praveendeviam.cattlebook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.praveendeviam.cattlebook.data.db.entity.MilkEntry
import io.github.praveendeviam.cattlebook.data.db.entity.MilkSession
import io.github.praveendeviam.cattlebook.data.db.entity.SettlementPeriod
import io.github.praveendeviam.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val fromDate: LocalDate = LocalDate.now(),
    val toDate: LocalDate = LocalDate.now(),
    val totalLitres: Double = 0.0,
    val todayMorning: Double? = null,
    val todayEvening: Double? = null,
    val recentEntries: List<MilkEntry> = emptyList(),
    val period: SettlementPeriod? = null,
    val showFromPicker: Boolean = false,
    val showToPicker: Boolean = false,
)

private data class RangeState(
    val fromOverride: LocalDate? = null,
    val toDate: LocalDate = LocalDate.now(),
    val showFromPicker: Boolean = false,
    val showToPicker: Boolean = false,
)

class HomeViewModel(private val repository: LedgerRepository) : ViewModel() {

    private val _range = MutableStateFlow(RangeState())

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getCurrentPeriod(),
        repository.getAllMilk(),
        _range
    ) { period, allEntries, range ->
        val from = range.fromOverride
            ?: period?.let { LocalDate.ofEpochDay(it.startDate) }
            ?: LocalDate.now()
        val fromEpoch = from.toEpochDay()
        val toEpoch = range.toDate.toEpochDay()
        val todayEpoch = LocalDate.now().toEpochDay()

        val todayEntries = allEntries.filter { it.date == todayEpoch }
        val rangeTotal = allEntries.filter { it.date in fromEpoch..toEpoch }.sumOf { it.litres }

        HomeUiState(
            fromDate = from,
            toDate = range.toDate,
            totalLitres = rangeTotal,
            todayMorning = todayEntries.find { it.session == MilkSession.MORNING }?.litres,
            todayEvening = todayEntries.find { it.session == MilkSession.EVENING }?.litres,
            recentEntries = allEntries.take(5),
            period = period,
            showFromPicker = range.showFromPicker,
            showToPicker = range.showToPicker,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun showFromPicker() = _range.update { it.copy(showFromPicker = true) }
    fun showToPicker()   = _range.update { it.copy(showToPicker = true) }

    fun onFromDateChange(date: LocalDate) =
        _range.update { it.copy(fromOverride = date, showFromPicker = false) }

    fun onToDateChange(date: LocalDate) =
        _range.update { it.copy(toDate = date, showToPicker = false) }

    fun dismissFromPicker() = _range.update { it.copy(showFromPicker = false) }
    fun dismissToPicker()   = _range.update { it.copy(showToPicker = false) }

    init {
        viewModelScope.launch { repository.ensureCurrentPeriod() }
    }

    companion object {
        fun factory(repository: LedgerRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(repository) }
        }
    }
}
