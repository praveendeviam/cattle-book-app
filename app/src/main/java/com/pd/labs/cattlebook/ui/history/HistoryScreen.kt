package com.pd.labs.cattlebook.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pd.labs.cattlebook.app
import com.pd.labs.cattlebook.data.db.entity.MilkEntry
import com.pd.labs.cattlebook.data.db.entity.MilkSession
import com.pd.labs.cattlebook.ui.theme.Amber100
import com.pd.labs.cattlebook.ui.theme.Amber700
import com.pd.labs.cattlebook.ui.theme.Green100
import com.pd.labs.cattlebook.ui.theme.Green700
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DateFmt = DateTimeFormatter.ofPattern("d MMM yyyy")

private fun LocalDate.toDisplayLabel(): String {
    val today = LocalDate.now()
    return when (this) {
        today -> "Today"
        today.minusDays(1) -> "Yesterday"
        else -> this.format(DateFmt)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onEditMilk: (Long) -> Unit = {},
    vm: HistoryViewModel = viewModel(factory = HistoryViewModel.factory(LocalContext.current.app.repository))
) {
    val entries by vm.milkEntries.collectAsStateWithLifecycle(emptyList())
    var entryToDelete: MilkEntry? by remember { mutableStateOf(null) }

    if (entryToDelete != null) {
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            title = { Text("Delete entry?") },
            text = { Text("This entry will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteMilk(entryToDelete!!)
                    entryToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { entryToDelete = null }) { Text("Cancel") }
            }
        )
    }

    val groupedByDate = remember(entries) {
        entries
            .groupBy { LocalDate.ofEpochDay(it.date) }
            .entries
            .sortedByDescending { (date, _) -> date }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { Text("Milk History", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Green700,
                titleContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        if (entries.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        "No milk entries yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                groupedByDate.forEach { (date, dayEntries) ->
                    stickyHeader(key = "header_$date") {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Text(
                                text = date.toDisplayLabel(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                            )
                        }
                    }
                    val sorted = dayEntries.sortedByDescending {
                        if (it.session == MilkSession.EVENING) 1 else 0
                    }
                    items(sorted, key = { it.id }) { entry ->
                        MilkCard(
                            entry = entry,
                            onEdit = { onEditMilk(entry.id) },
                            onDelete = { entryToDelete = entry }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MilkCard(entry: MilkEntry, onEdit: () -> Unit, onDelete: () -> Unit) {
    val isMorning = entry.session == MilkSession.MORNING

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Session indicator
            Surface(
                color = if (isMorning) Amber100 else Green100,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (isMorning) "Morning" else "Evening",
                    color = if (isMorning) Amber700 else Green700,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            // Litres — primary info
            Text(
                "${"%.1f".format(entry.litres)} L",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            entry.note?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }

            IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Green700,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
