package io.github.praveendeviam.cattlebook.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.praveendeviam.cattlebook.app
import io.github.praveendeviam.cattlebook.data.db.entity.MilkEntry
import io.github.praveendeviam.cattlebook.data.db.entity.MilkSession
import io.github.praveendeviam.cattlebook.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val HeaderFmt = DateTimeFormatter.ofPattern("MMMM yyyy")
private val DetailFmt = DateTimeFormatter.ofPattern("EEEE, d MMM yyyy")
private val DOW_LABELS = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onEditMilk: (Long) -> Unit = {},
    vm: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(LocalContext.current.app.repository))
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var entryToDelete by remember { mutableStateOf<MilkEntry?>(null) }

    val entriesByDate = remember(state.allEntries) {
        state.allEntries.groupBy { LocalDate.ofEpochDay(it.date) }
    }
    val selectedEntries = remember(state.selectedDate, entriesByDate) {
        (entriesByDate[state.selectedDate] ?: emptyList())
            .sortedByDescending { if (it.session == MilkSession.EVENING) 1 else 0 }
    }

    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete entry?") },
            text = { Text("This entry will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = { vm.deleteMilk(entryToDelete!!); entryToDelete = null }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { entryToDelete = null }) { Text("Cancel") } }
        )
    }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        TopAppBar(
            title = { Text("Milk History", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Green700, titleContentColor = Color.White)
        )

        // ── Calendar (fixed) ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Month nav
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = vm::prevMonth, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronLeft, null, tint = Green700)
                }
                Text(
                    state.month.format(HeaderFmt),
                    Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = vm::nextMonth, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.ChevronRight, null, tint = Green700)
                }
            }

            // Day of week labels
            Row(Modifier.fillMaxWidth()) {
                DOW_LABELS.forEach { label ->
                    Text(
                        label,
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }

            CalendarGrid(
                yearMonth = state.month,
                entriesByDate = entriesByDate,
                selectedDate = state.selectedDate,
                today = LocalDate.now(),
                onDateSelected = vm::selectDate
            )
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))

        // ── Entry list (scrollable) ───────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    state.selectedDate.format(DetailFmt),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            if (selectedEntries.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.WaterDrop, null, tint = Green700.copy(0.15f), modifier = Modifier.size(40.dp))
                        Text("No entries on this day", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.35f))
                    }
                }
            } else {
                items(selectedEntries, key = { it.id }) { entry ->
                    MilkCard(entry = entry, onEdit = { onEditMilk(entry.id) }, onDelete = { entryToDelete = entry })
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    entriesByDate: Map<LocalDate, List<MilkEntry>>,
    selectedDate: LocalDate,
    today: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val offset = yearMonth.atDay(1).dayOfWeek.value - 1  // Mon=0 … Sun=6
    val rows = (offset + daysInMonth + 6) / 7

    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        repeat(rows) { row ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { col ->
                    val day = row * 7 + col - offset + 1
                    if (day < 1 || day > daysInMonth) {
                        Box(Modifier.weight(1f).height(46.dp))
                    } else {
                        val date = yearMonth.atDay(day)
                        val entries = entriesByDate[date] ?: emptyList()
                        DayCell(
                            modifier = Modifier.weight(1f),
                            day = day,
                            hasMorning = entries.any { it.session == MilkSession.MORNING },
                            hasEvening = entries.any { it.session == MilkSession.EVENING },
                            isToday = date == today,
                            isSelected = date == selectedDate,
                            onClick = { onDateSelected(date) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    modifier: Modifier,
    day: Int,
    hasMorning: Boolean,
    hasEvening: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(modifier = modifier.height(46.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(when { isSelected -> Green700; isToday -> Green100; else -> Color.Transparent })
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "$day",
                    fontSize = 13.sp,
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                    color = when { isSelected -> Color.White; isToday -> Green700; else -> OnSurface }
                )
                if (hasMorning || hasEvening) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        if (hasMorning) Box(Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White.copy(0.9f) else Amber500))
                        if (hasEvening) Box(Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White.copy(0.7f) else Teal600))
                    }
                }
            }
        }
    }
}

@Composable
private fun MilkCard(entry: MilkEntry, onEdit: () -> Unit, onDelete: () -> Unit) {
    val isMorning = entry.session == MilkSession.MORNING
    val accent = if (isMorning) Amber700 else Teal700
    val pillBg = if (isMorning) Amber100 else Teal100

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(4.dp).height(64.dp).clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)).background(accent))
            Row(Modifier.weight(1f).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = pillBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        if (isMorning) "Morning" else "Evening",
                        color = accent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text("${"%.1f".format(entry.litres)} L", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                entry.note?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.45f), modifier = Modifier.padding(end = 4.dp)) }
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, null, tint = Green700, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
