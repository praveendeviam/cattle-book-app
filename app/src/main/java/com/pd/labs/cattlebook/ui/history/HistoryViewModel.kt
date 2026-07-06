package com.pd.labs.cattlebook.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pd.labs.cattlebook.data.db.entity.MilkEntry
import com.pd.labs.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: LedgerRepository) : ViewModel() {

    val milkEntries = repository.getAllMilk().map { list ->
        list.sortedByDescending { it.date * 2 + if (it.session.name == "EVENING") 1 else 0 }
    }

    fun deleteMilk(entry: MilkEntry) = viewModelScope.launch { repository.deleteMilkEntry(entry) }

    companion object {
        fun factory(repository: LedgerRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { HistoryViewModel(repository) }
        }
    }
}
