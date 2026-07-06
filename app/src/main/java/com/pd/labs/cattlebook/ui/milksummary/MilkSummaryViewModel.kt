package com.pd.labs.cattlebook.ui.milksummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pd.labs.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class MilkSummaryState(
    val startDate: LocalDate = LocalDate.now().minusDays(6),
    val endDate: LocalDate = LocalDate.now(),
    val ratePerLitre: String = "",
    val totalLitres: Double = 0.0,
)

class MilkSummaryViewModel(private val repository: LedgerRepository) : ViewModel() {

    private val _state = MutableStateFlow(MilkSummaryState())
    val state: StateFlow<MilkSummaryState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.map { it.startDate to it.endDate }
                .distinctUntilChanged()
                .collect { (start, end) ->
                    val litres = repository.getTotalLitresInRange(start.toEpochDay(), end.toEpochDay())
                    _state.update { it.copy(totalLitres = litres) }
                }
        }
    }

    fun onStartDateChange(d: LocalDate) = _state.update { it.copy(startDate = d) }
    fun onEndDateChange(d: LocalDate) = _state.update { it.copy(endDate = d) }
    fun onRateChange(v: String) = _state.update { it.copy(ratePerLitre = v) }

    companion object {
        fun factory(repository: LedgerRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { MilkSummaryViewModel(repository) }
        }
    }
}
