package com.pd.labs.cattlebook.ui.paymenthistory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pd.labs.cattlebook.app
import com.pd.labs.cattlebook.data.db.entity.SettlementPeriod
import com.pd.labs.cattlebook.ui.theme.Green100
import com.pd.labs.cattlebook.ui.theme.Green700
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DateFmt = DateTimeFormatter.ofPattern("d MMM yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    vm: PaymentHistoryViewModel = viewModel(
        factory = PaymentHistoryViewModel.factory(LocalContext.current.app.repository)
    )
) {
    val payments by vm.payments.collectAsStateWithLifecycle(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { Text("Payment History", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Green700,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        if (payments.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No payments recorded yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(payments, key = { it.id }) { period ->
                    PaymentCard(period)
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(period: SettlementPeriod) {
    val start = LocalDate.ofEpochDay(period.startDate).format(DateFmt)
    val end = period.endDate?.let { LocalDate.ofEpochDay(it).format(DateFmt) } ?: "—"
    val paidOn = period.paymentDate?.let { LocalDate.ofEpochDay(it).format(DateFmt) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Green100)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Green700,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "$start – $end",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Green700.copy(alpha = 0.75f)
                )
                Text(
                    text = "₹ %,.2f".format(period.paymentAmount ?: 0.0),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Green700
                )
                paidOn?.let {
                    Text(
                        "Paid on $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = Green700.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
