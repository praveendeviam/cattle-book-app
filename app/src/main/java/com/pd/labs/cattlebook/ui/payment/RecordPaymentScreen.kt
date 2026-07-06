package com.pd.labs.cattlebook.ui.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pd.labs.cattlebook.app
import com.pd.labs.cattlebook.ui.common.DateField
import com.pd.labs.cattlebook.ui.theme.Green100
import com.pd.labs.cattlebook.ui.theme.Green700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentScreen(
    onBack: () -> Unit,
    vm: RecordPaymentViewModel = viewModel(
        factory = RecordPaymentViewModel.factory(LocalContext.current.app.repository)
    )
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green700,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Step 1 — Select date range", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)

            DateField(
                label = "From Date",
                date = state.startDate,
                onDateSelected = vm::onStartDateChange,
                modifier = Modifier.fillMaxWidth()
            )
            DateField(
                label = "To Date",
                date = state.endDate,
                onDateSelected = vm::onEndDateChange,
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Green100)
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WaterDrop, contentDescription = null,
                        tint = Green700, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Milk collected in range", style = MaterialTheme.typography.bodyMedium,
                            color = Green700.copy(alpha = 0.8f))
                        Text(
                            "${"%.1f".format(state.totalLitres)} L",
                            fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Green700
                        )
                    }
                }
            }

            HorizontalDivider()

            Text("Step 2 — Enter rate & amount", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)

            OutlinedTextField(
                value = state.ratePerLitre,
                onValueChange = vm::onRateChange,
                label = { Text("Rate per Litre (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true,
                supportingText = if (state.ratePerLitre.isNotBlank() && state.ratePerLitre.toDoubleOrNull() != null)
                    {{ Text("${"%.1f".format(state.totalLitres)} L × ₹${state.ratePerLitre}/L") }} else null
            )

            OutlinedTextField(
                value = state.amount,
                onValueChange = vm::onAmountChange,
                label = { Text("Amount Received (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true
            )

            HorizontalDivider()

            Text("Step 3 — Payment date", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)

            DateField(
                label = "Payment Date",
                date = state.paymentDate,
                onDateSelected = vm::onPaymentDateChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            val canSave = state.amount.toDoubleOrNull() != null
            Button(
                onClick = { vm.save(onBack) },
                enabled = canSave && !state.saving,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green700)
            ) {
                Text("Confirm & Close Period", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                "This will close the current period and start a new one.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
