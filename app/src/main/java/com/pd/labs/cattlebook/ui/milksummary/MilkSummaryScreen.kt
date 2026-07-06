package com.pd.labs.cattlebook.ui.milksummary

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
fun MilkSummaryScreen(
    onBack: () -> Unit,
    vm: MilkSummaryViewModel = viewModel(
        factory = MilkSummaryViewModel.factory(LocalContext.current.app.repository)
    )
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val rate = state.ratePerLitre.toDoubleOrNull()
    val earnings = if (rate != null) state.totalLitres * rate else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Milk Summary", fontWeight = FontWeight.Bold) },
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
            Text(
                "Select a date range to see how many litres were collected.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

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
                    Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Green700,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Milk Collected",
                            style = MaterialTheme.typography.titleMedium,
                            color = Green700.copy(alpha = 0.8f)
                        )
                        Text(
                            "${"%.1f".format(state.totalLitres)} L",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Green700
                        )
                    }
                }
            }

            HorizontalDivider()

            Text(
                "Calculate Earnings (optional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = state.ratePerLitre,
                onValueChange = vm::onRateChange,
                label = { Text("Rate per Litre (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true
            )

            if (earnings != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "${"%.1f".format(state.totalLitres)} L  ×  ₹${state.ratePerLitre}/L",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            "= ₹ %,.2f".format(earnings),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
