package com.pd.labs.cattlebook.ui.addmilk

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pd.labs.cattlebook.app
import com.pd.labs.cattlebook.data.db.entity.MilkSession
import com.pd.labs.cattlebook.ui.common.DateField
import com.pd.labs.cattlebook.ui.theme.Green700

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
    val title = if (state.isEditMode) "Edit Milk Entry" else "Add Milk"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green700,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DateField(
                label = "Date",
                date = state.date,
                onDateSelected = vm::onDateChange,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Session", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = state.session == MilkSession.MORNING,
                    onClick = { vm.onSessionChange(MilkSession.MORNING) },
                    label = { Text("Morning", fontSize = 17.sp) },
                    modifier = Modifier.weight(1f).height(52.dp)
                )
                FilterChip(
                    selected = state.session == MilkSession.EVENING,
                    onClick = { vm.onSessionChange(MilkSession.EVENING) },
                    label = { Text("Evening", fontSize = 17.sp) },
                    modifier = Modifier.weight(1f).height(52.dp)
                )
            }

            OutlinedTextField(
                value = state.litres,
                onValueChange = vm::onLitresChange,
                label = { Text("Litres") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true,
                suffix = { Text("L", fontSize = 18.sp) }
            )

            OutlinedTextField(
                value = state.note,
                onValueChange = vm::onNoteChange,
                label = { Text("Note — optional") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { vm.save(onBack) },
                enabled = state.litres.toDoubleOrNull() != null && !state.saving,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green700)
            ) {
                Text(
                    if (state.isEditMode) "Update Entry" else "Save Entry",
                    fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
