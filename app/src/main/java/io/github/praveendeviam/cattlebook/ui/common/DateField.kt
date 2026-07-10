package io.github.praveendeviam.cattlebook.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DisplayFmt = DateTimeFormatter.ofPattern("d MMM yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = date.format(DisplayFmt),
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        trailingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Pick date") },
        modifier = modifier.clickable { showPicker = true },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    )

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.toEpochDay() * 86_400_000L
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(LocalDate.ofEpochDay(millis / 86_400_000L))
                    }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
