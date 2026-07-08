package com.pd.labs.cattlebook.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.time.temporal.ChronoUnit

private val DateFmt = DateTimeFormatter.ofPattern("d MMM")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSettings: () -> Unit,
    vm: HomeViewModel = viewModel(factory = HomeViewModel.factory(LocalContext.current.app.repository))
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    val period = state.period
    val totalLitres = state.totalLitres

    val today = LocalDate.now()
    val periodStart = period?.let { LocalDate.ofEpochDay(it.startDate) }
    val periodEnd = period?.endDate?.let { LocalDate.ofEpochDay(it) }
    val daysInPeriod = periodStart?.let { ChronoUnit.DAYS.between(it, today) + 1 } ?: 1L
    val avgPerDay = if (daysInPeriod > 0) totalLitres / daysInPeriod else 0.0
    val startLabel = periodStart?.format(DateFmt) ?: "—"
    val endLabel = periodEnd?.format(DateFmt) ?: "Today"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { Text("CattleBook", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Green700,
                titleContentColor = Color.White
            ),
            actions = {
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero card — dark green with white text
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = Green700),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            "$startLabel – $endLabel",
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "MILK COLLECTED",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 2.sp
                    )
                    Text(
                        "${"%.1f".format(totalLitres)} L",
                        color = Color.White,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 64.sp
                    )
                    Text(
                        "litres this period",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Stat cards row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "$daysInPeriod",
                    label = "days in period"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "${"%.1f".format(avgPerDay)} L",
                    label = "avg per day"
                )
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, label: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Green100)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                value,
                color = Green700,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )
            Text(
                label,
                color = Green700.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
