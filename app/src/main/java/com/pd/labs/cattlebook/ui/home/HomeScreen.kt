package com.pd.labs.cattlebook.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pd.labs.cattlebook.app
import com.pd.labs.cattlebook.ui.theme.Green100
import com.pd.labs.cattlebook.ui.theme.Green700
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DateFmt = DateTimeFormatter.ofPattern("d MMM")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddMilk: () -> Unit,
    onAddFeed: () -> Unit,
    onHistory: () -> Unit,
    onRecordPayment: () -> Unit,
    onMilkSummary: () -> Unit,
    onPaymentHistory: () -> Unit,
    onSettings: () -> Unit,
    vm: HomeViewModel = viewModel(factory = HomeViewModel.factory(LocalContext.current.app.repository))
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CattleBook", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green700,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onHistory) {
                        Icon(Icons.Default.History, contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.period?.let { period ->
                val start = LocalDate.ofEpochDay(period.startDate).format(DateFmt)
                val end = period.endDate?.let { LocalDate.ofEpochDay(it).format(DateFmt) } ?: "Today"
                Text(
                    text = "Period: $start – $end",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Green100)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Milk Collected", style = MaterialTheme.typography.titleMedium,
                        color = Green700.copy(alpha = 0.8f))
                    Text(
                        text = "${"%.1f".format(state.totalLitres)} L",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Green700
                    )
                    Text("litres this period",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Green700.copy(alpha = 0.6f))
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onAddMilk,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green700)
            ) {
                Text("+ Add Milk", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onMilkSummary,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Milk Summary / Calculate Earnings", fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = onPaymentHistory,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Payment History", fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = onRecordPayment,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.Payments, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Record Payment Received", fontSize = 16.sp)
            }
        }
    }
}
