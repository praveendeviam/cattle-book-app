package com.pd.labs.cattlebook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pd.labs.cattlebook.data.db.entity.MilkEntry
import com.pd.labs.cattlebook.data.db.entity.MilkSession
import com.pd.labs.cattlebook.data.db.entity.SettlementPeriod
import com.pd.labs.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val period: SettlementPeriod? = null,
    val totalLitres: Double = 0.0,
    val todayMorning: Double? = null,
    val todayEvening: Double? = null,
    val recentEntries: List<MilkEntry> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repository: LedgerRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getCurrentPeriod(),
        repository.getAllMilk()
    ) { period, allEntries ->
        period to allEntries
    }.flatMapLatest { (period, allEntries) ->
        val todayEpoch = LocalDate.now().toEpochDay()
        val todayEntries = allEntries.filter { it.date == todayEpoch }
        val morning = todayEntries.find { it.session == MilkSession.MORNING }?.litres
        val evening = todayEntries.find { it.session == MilkSession.EVENING }?.litres
        val recent = allEntries.take(5)

        if (period == null) {
            flowOf(HomeUiState(todayMorning = morning, todayEvening = evening, recentEntries = recent))
        } else {
            repository.getTotalLitres(period.id).map { litres ->
                HomeUiState(
                    period = period,
                    totalLitres = litres,
                    todayMorning = morning,
                    todayEvening = evening,
                    recentEntries = recent
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch { repository.ensureCurrentPeriod() }
    }

    companion object {
        fun factory(repository: LedgerRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(repository) }
        }
    }
}
