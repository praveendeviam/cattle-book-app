package com.pd.labs.cattlebook.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
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
import com.pd.labs.cattlebook.data.db.entity.MilkEntry
import com.pd.labs.cattlebook.data.db.entity.MilkSession
import com.pd.labs.cattlebook.ui.theme.Amber200
import com.pd.labs.cattlebook.ui.theme.Amber50
import com.pd.labs.cattlebook.ui.theme.Amber700
import com.pd.labs.cattlebook.ui.theme.Green50
import com.pd.labs.cattlebook.ui.theme.Green600
import com.pd.labs.cattlebook.ui.theme.Green700
import com.pd.labs.cattlebook.ui.theme.Green800
import com.pd.labs.cattlebook.ui.theme.Teal100
import com.pd.labs.cattlebook.ui.theme.Teal50
import com.pd.labs.cattlebook.ui.theme.Teal700
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val DateFmt = DateTimeFormatter.ofPattern("d MMM")
private val EntryDateFmt = DateTimeFormatter.ofPattern("d MMM")

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

    val today = LocalDate.now()
    val periodStart = period?.let { LocalDate.ofEpochDay(it.startDate) }
    val periodEnd = period?.endDate?.let { LocalDate.ofEpochDay(it) }
    val daysInPeriod = periodStart?.let { ChronoUnit.DAYS.between(it, today) + 1 } ?: 1L
    val avgPerDay = if (daysInPeriod > 0) state.totalLitres / daysInPeriod else 0.0
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Hero card ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
                    .background(HeroGradient, RoundedCornerShape(20.dp))
                    .padding(horizontal = 22.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            "$startLabel – $endLabel",
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "TOTAL THIS PERIOD",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "%.1f".format(state.totalLitres),
                            color = Color.White,
                            fontSize = 52.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 56.sp
                        )
                        Text(
                            "L",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Text(
                        "litres collected",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // ── Today's milk ──────────────────────────────────────────────
            SectionTitle("TODAY'S MILK")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SessionStatusCard(
                    modifier = Modifier.weight(1f),
                    label = "Morning",
                    litres = state.todayMorning,
                    accentColor = Amber700,
                    bgColor = Amber50,
                    pillBg = Amber200
                )
                SessionStatusCard(
                    modifier = Modifier.weight(1f),
                    label = "Evening",
                    litres = state.todayEvening,
                    accentColor = Teal700,
                    bgColor = Teal50,
                    pillBg = Teal100
                )
            }

            // ── Period stats ──────────────────────────────────────────────
            SectionTitle("THIS PERIOD")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "$daysInPeriod",
                    label = "days active",
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

            // ── Recent entries ────────────────────────────────────────────
            if (state.recentEntries.isNotEmpty()) {
                SectionTitle("RECENT ENTRIES")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.recentEntries.forEach { entry ->
                        RecentEntryCard(entry)
                    }
                }
            }

            // Space so FAB doesn't cover last card
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.40f),
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun SessionStatusCard(
    modifier: Modifier,
    label: String,
    litres: Double?,
    accentColor: Color,
    bgColor: Color,
    pillBg: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(color = pillBg, shape = RoundedCornerShape(6.dp)) {
                Text(
                    label,
                    color = accentColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            if (litres != null) {
                Text(
                    "${"%.1f".format(litres)} L",
                    color = accentColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Green700,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        "Logged",
                        color = Green700,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            } else {
                Text(
                    "—",
                    color = accentColor.copy(alpha = 0.25f),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Not logged",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                    style = MaterialTheme.typography.labelSmall
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
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

@Composable
private fun RecentEntryCard(entry: MilkEntry) {
    val isMorning = entry.session == MilkSession.MORNING
    val accentColor = if (isMorning) Amber700 else Teal700
    val pillBg = if (isMorning) Amber200 else Teal100
    val dateStr = LocalDate.ofEpochDay(entry.date).format(EntryDateFmt)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(color = pillBg, shape = RoundedCornerShape(6.dp)) {
                Text(
                    if (isMorning) "Morning" else "Evening",
                    color = accentColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Text(
                dateStr,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.WaterDrop,
                contentDescription = null,
                tint = accentColor.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
            Text(
                "${"%.1f".format(entry.litres)} L",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
