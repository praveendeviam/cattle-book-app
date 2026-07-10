package io.github.praveendeviam.cattlebook.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.github.praveendeviam.cattlebook.data.repository.LedgerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PaymentState(
    val startDate: LocalDate = LocalDate.now().minusDays(13),
    val endDate: LocalDate = LocalDate.now(),
    val totalLitres: Double = 0.0,
    val ratePerLitre: String = "",
    val amount: String = "",
    val paymentDate: LocalDate = LocalDate.now(),
    val saving: Boolean = false,
)

class RecordPaymentViewModel(private val repository: LedgerRepository) : ViewModel() {
    private val _state = MutableStateFlow(PaymentState())
    val state: StateFlow<PaymentState> = _state.asStateFlow()

    val currentPeriod = repository.getCurrentPeriod()

    init {
        viewModelScope.launch {
            repository.getCurrentPeriod().collect { period ->
                period?.let { p ->
                    _state.update { it.copy(startDate = LocalDate.ofEpochDay(p.startDate)) }
                }
            }
        }

        viewModelScope.launch {
            _state.map { it.startDate to it.endDate }
                .distinctUntilChanged()
                .collect { (start, end) ->
                    val litres = repository.getTotalLitresInRange(start.toEpochDay(), end.toEpochDay())
                    _state.update { it.copy(totalLitres = litres) }
                    recomputeAmount()
                }
        }
    }

    fun onStartDateChange(d: LocalDate) = _state.update { it.copy(startDate = d) }
    fun onEndDateChange(d: LocalDate) = _state.update { it.copy(endDate = d) }
    fun onPaymentDateChange(d: LocalDate) = _state.update { it.copy(paymentDate = d) }

    fun onRateChange(v: String) {
        _state.update { it.copy(ratePerLitre = v) }
        recomputeAmount()
    }

    fun onAmountChange(v: String) = _state.update { it.copy(amount = v) }

    private fun recomputeAmount() {
        val s = _state.value
        val rate = s.ratePerLitre.toDoubleOrNull() ?: return
        if (s.totalLitres > 0) {
            _state.update { it.copy(amount = "%.2f".format(s.totalLitres * rate)) }
        }
    }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        val amount = s.amount.toDoubleOrNull() ?: return
        _state.update { it.copy(saving = true) }
        viewModelScope.launch {
            repository.recordPayment(amount, s.paymentDate)
            onDone()
        }
    }

    companion object {
        fun factory(repository: LedgerRepository): ViewModelProvider.Factory = viewModelFactory {
            initializer { RecordPaymentViewModel(repository) }
        }
    }
}
