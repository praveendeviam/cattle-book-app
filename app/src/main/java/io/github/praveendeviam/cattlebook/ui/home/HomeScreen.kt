package io.github.praveendeviam.cattlebook.ui.home

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.praveendeviam.cattlebook.R
import io.github.praveendeviam.cattlebook.app
import io.github.praveendeviam.cattlebook.data.db.entity.MilkEntry
import io.github.praveendeviam.cattlebook.data.db.entity.MilkSession
import io.github.praveendeviam.cattlebook.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val DateFmt  = DateTimeFormatter.ofPattern("d MMM")
private val DateFmtY = DateTimeFormatter.ofPattern("d MMM yyyy")

private val HeroGradient = Brush.linearGradient(
    colors = listOf(Green800, Green600),
    start = Offset.Zero,
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

private fun LocalDate.toUtcMillis(): Long =
    this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

private fun LocalDate.smart(): String {
    val today = LocalDate.now()
    return if (this.year == today.year) this.format(DateFmt) else this.format(DateFmtY)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSettings: () -> Unit,
    vm: HomeViewModel = viewModel(factory = HomeViewModel.factory(LocalContext.current.app.repository))
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    val currentLang = remember { prefs.getString("language", "en") ?: "en" }

    val state by vm.uiState.collectAsStateWithLifecycle()
    val days = remember(state.fromDate, state.toDate) {
        ChronoUnit.DAYS.between(state.fromDate, state.toDate) + 1
    }
    val avg = if (days > 0) state.totalLitres / days else 0.0

    // ── From picker ───────────────────────────────────────────────────────────
    if (state.showFromPicker) {
        val dpState = rememberDatePickerState(
            initialSelectedDateMillis = state.fromDate.toUtcMillis()
        )
        DatePickerDialog(
            onDismissRequest = vm::dismissFromPicker,
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.toLocalDate()?.let(vm::onFromDateChange)
                        ?: vm.dismissFromPicker()
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = { TextButton(onClick = vm::dismissFromPicker) { Text(stringResource(R.string.cancel)) } }
        ) { DatePicker(state = dpState) }
    }

    // ── To picker ─────────────────────────────────────────────────────────────
    if (state.showToPicker) {
        val dpState = rememberDatePickerState(
            initialSelectedDateMillis = state.toDate.toUtcMillis()
        )
        DatePickerDialog(
            onDismissRequest = vm::dismissToPicker,
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.toLocalDate()?.let(vm::onToDateChange)
                        ?: vm.dismissToPicker()
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = { TextButton(onClick = vm::dismissToPicker) { Text(stringResource(R.string.cancel)) } }
        ) { DatePicker(state = dpState) }
    }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        TopAppBar(
            title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Green700,
                titleContentColor = Color.White
            ),
            actions = {
                Surface(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            val newLang = if (currentLang == "en") "ta" else "en"
                            prefs.edit().putString("language", newLang).apply()
                            (context as? Activity)?.recreate()
                        },
                    color = Color.White.copy(alpha = 0.20f)
                ) {
                    Text(
                        if (currentLang == "en") "EN" else "த",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, null, tint = Color.White)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // ── Period selector card ──────────────────────────────────────────
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        stringResource(R.string.viewing_period),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        letterSpacing = 1.5.sp
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DateChip(Modifier.weight(1f), stringResource(R.string.label_from), state.fromDate.smart(), vm::showFromPicker)
                        Icon(
                            Icons.Default.ArrowForward, null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier.size(16.dp)
                        )
                        DateChip(Modifier.weight(1f), stringResource(R.string.label_to), state.toDate.smart(), vm::showToPicker)
                    }
                }
            }

            // ── Hero: total collected ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .background(HeroGradient, RoundedCornerShape(20.dp))
                    .padding(horizontal = 22.dp, vertical = 20.dp)
            ) {
                Column {
                    Text(
                        stringResource(R.string.total_collected),
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp
                    )
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "%.1f".format(state.totalLitres),
                            color = Color.White,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 60.sp
                        )
                        Text(
                            "L",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 9.dp)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "$days days",
                            color = Color.White.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text("·", color = Color.White.copy(alpha = 0.4f), style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${"%.1f".format(avg)} L/day avg",
                            color = Color.White.copy(alpha = 0.65f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // ── Today's milk ──────────────────────────────────────────────────
            SectionLabel(stringResource(R.string.todays_milk))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SessionCard(Modifier.weight(1f), stringResource(R.string.morning), state.todayMorning, Amber700, Amber50, Amber100)
                SessionCard(Modifier.weight(1f), stringResource(R.string.evening), state.todayEvening, Teal700, Teal50, Teal100)
            }

            // ── Recent entries ────────────────────────────────────────────────
            if (state.recentEntries.isNotEmpty()) {
                SectionLabel(stringResource(R.string.recent_entries))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.recentEntries.forEach { RecentRow(it) }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun DateChip(modifier: Modifier, label: String, date: String, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(10.dp)).clickable(onClick = onClick),
        color = Green50,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.DateRange, null, tint = Green700, modifier = Modifier.size(16.dp))
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Green700.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Green700)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.40f),
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun SessionCard(modifier: Modifier, label: String, litres: Double?, accent: Color, bg: Color, pillBg: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(color = pillBg, shape = RoundedCornerShape(6.dp)) {
                Text(
                    label, color = accent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            if (litres != null) {
                Text("${"%.1f".format(litres)} L", color = accent, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.CheckCircle, null, tint = Green700, modifier = Modifier.size(13.dp))
                    Text(stringResource(R.string.logged), color = Green700, style = MaterialTheme.typography.labelSmall)
                }
            } else {
                Text("—", color = accent.copy(alpha = 0.22f), fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.not_logged), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun RecentRow(entry: MilkEntry) {
    val isMorning = entry.session == MilkSession.MORNING
    val accent = if (isMorning) Amber700 else Teal700
    val pillBg = if (isMorning) Amber100 else Teal100
    val dateStr = LocalDate.ofEpochDay(entry.date).format(DateFmt)
    val sessionLabel = if (isMorning) stringResource(R.string.morning) else stringResource(R.string.evening)

    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(color = pillBg, shape = RoundedCornerShape(6.dp)) {
                Text(
                    sessionLabel,
                    color = accent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Text(dateStr, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Icon(Icons.Default.WaterDrop, null, tint = accent.copy(0.5f), modifier = Modifier.size(14.dp))
            Text("${"%.1f".format(entry.litres)} L", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}
