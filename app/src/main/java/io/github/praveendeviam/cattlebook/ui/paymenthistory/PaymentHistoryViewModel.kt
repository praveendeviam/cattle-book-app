package io.github.praveendeviam.cattlebook.ui.paymenthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.praveendeviam.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.flow.map

class PaymentHistoryViewModel(repository: LedgerRepository) : ViewModel() {

    val payments = repository.getAllPeriods().map { periods ->
        periods.filter { it.isClosed && it.paymentAmount != null }
            .sortedByDescending { it.paymentDate ?: it.endDate ?: it.startDate }
    }

    companion object {
        fun factory(repository: LedgerRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { PaymentHistoryViewModel(repository) }
        }
    }
}
