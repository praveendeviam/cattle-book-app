package com.pd.labs.cattlebook.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pd.labs.cattlebook.app
import com.pd.labs.cattlebook.ui.theme.Amber50
import com.pd.labs.cattlebook.ui.theme.Amber700
import com.pd.labs.cattlebook.ui.theme.Green50
import com.pd.labs.cattlebook.ui.theme.Green600
import com.pd.labs.cattlebook.ui.theme.Green700
import com.pd.labs.cattlebook.ui.theme.Green800
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val DateFmt = DateTimeFormatter.ofPattern("d MMM")

private val HeroGradient = Brush.linearGradient(
    colors = listOf(Green800, Green600),
    start = Offset.Zero,
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

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
            // Gradient hero card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
                    .background(HeroGradient, RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 22.dp)
            ) {
                Column {
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

                    Spacer(Modifier.height(14.dp))

                    Text(
                        "MILK COLLECTED",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 2.sp
                    )
                    Text(
                        "${"%.1f".format(totalLitres)} L",
                        color = Color.White,
                        fontSize = 58.sp,
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

            // Stat cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "$daysInPeriod",
                    label = "days in period",
                    valueColor = Green700,
                    containerColor = Green50
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "${"%.1f".format(avgPerDay)} L",
                    label = "avg per day",
                    valueColor = Amber700,
                    containerColor = Amber50
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier,
    value: String,
    label: String,
    valueColor: Color,
    containerColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                value,
                color = valueColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )
            Text(
                label,
                color = valueColor.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
