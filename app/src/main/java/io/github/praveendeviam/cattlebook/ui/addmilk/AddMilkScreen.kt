package io.github.praveendeviam.cattlebook.ui.addmilk

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.praveendeviam.cattlebook.app
import io.github.praveendeviam.cattlebook.data.db.entity.MilkSession
import io.github.praveendeviam.cattlebook.ui.common.DateField
import io.github.praveendeviam.cattlebook.ui.theme.Amber200
import io.github.praveendeviam.cattlebook.ui.theme.Amber700
import io.github.praveendeviam.cattlebook.ui.theme.Amber800
import io.github.praveendeviam.cattlebook.ui.theme.Green700
import io.github.praveendeviam.cattlebook.ui.theme.Teal100
import io.github.praveendeviam.cattlebook.ui.theme.Teal700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMilkScreen(
    onBack: () -> Unit,
    entryId: Long? = null,
    vm: AddMilkViewModel = viewModel(
        key = "milk_${entryId ?: "new"}",
        factory = AddMilkViewModel.factory(LocalContext.current.app.repository, entryId)
    )
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val isEdit = state.isEditMode

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEdit) "Edit Milk Entry" else "Add Milk",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green700,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DateField(
                label = "Date",
                date = state.date,
                onDateSelected = vm::onDateChange,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = state.session == MilkSession.MORNING,
                        onClick = { vm.onSessionChange(MilkSession.MORNING) },
                        label = { Text("Morning", fontSize = 16.sp, fontWeight = FontWeight.Medium) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Amber200,
                            selectedLabelColor = Amber800,
                            selectedLeadingIconColor = Amber800
                        )
                    )
                    FilterChip(
                        selected = state.session == MilkSession.EVENING,
                        onClick = { vm.onSessionChange(MilkSession.EVENING) },
                        label = { Text("Evening", fontSize = 16.sp, fontWeight = FontWeight.Medium) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Teal100,
                            selectedLabelColor = Teal700,
                            selectedLeadingIconColor = Teal700
                        )
                    )
                }
            }

            OutlinedTextField(
                value = state.litres,
                onValueChange = vm::onLitresChange,
                label = { Text("Litres") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true,
                suffix = { Text("L", fontSize = 18.sp, color = Green700, fontWeight = FontWeight.SemiBold) }
            )

            OutlinedTextField(
                value = state.note,
                onValueChange = vm::onNoteChange,
                label = { Text("Note — optional") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = { vm.save(onBack) },
                enabled = state.litres.toDoubleOrNull() != null && !state.saving,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green700),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    if (isEdit) "Update Entry" else "Save Entry",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
