package com.pd.labs.cattlebook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pd.labs.cattlebook.data.db.entity.SettlementPeriod
import com.pd.labs.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val period: SettlementPeriod? = null,
    val totalLitres: Double = 0.0,
)

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repository: LedgerRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.getCurrentPeriod()
        .flatMapLatest { period ->
            if (period == null) {
                flowOf(HomeUiState())
            } else {
                repository.getTotalLitres(period.id).map { litres ->
                    HomeUiState(period = period, totalLitres = litres)
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch { repository.ensureCurrentPeriod() }
    }

    companion object {
        fun factory(repository: LedgerRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HomeViewModel(repository) }
        }
    }
}
