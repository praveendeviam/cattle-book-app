package com.pd.labs.cattlebook.ui.addfeed

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
import com.pd.labs.cattlebook.ui.common.DateField
import com.pd.labs.cattlebook.ui.theme.Amber700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeedScreen(
    onBack: () -> Unit,
    entryId: Long? = null,
    vm: AddFeedViewModel = viewModel(
        key = "feed_${entryId ?: "new"}",
        factory = AddFeedViewModel.factory(LocalContext.current.app.repository, entryId)
    )
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val title = if (state.isEditMode) "Edit Feed Entry" else "Add Feed"

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
                    containerColor = Amber700,
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

            OutlinedTextField(
                value = state.item,
                onValueChange = vm::onItemChange,
                label = { Text("Item") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
                singleLine = true
            )

            OutlinedTextField(
                value = state.bags,
                onValueChange = vm::onBagsChange,
                label = { Text("Number of bags") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true
            )

            OutlinedTextField(
                value = state.pricePerBag,
                onValueChange = vm::onPriceChange,
                label = { Text("Price per bag (₹) — optional") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true
            )

            OutlinedTextField(
                value = state.amount,
                onValueChange = vm::onAmountChange,
                label = { Text("Total Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true
            )

            OutlinedTextField(
                value = state.note,
                onValueChange = vm::onNoteChange,
                label = { Text("Note — optional") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(Modifier.height(8.dp))

            val canSave = state.bags.toIntOrNull() != null && state.amount.toDoubleOrNull() != null
            Button(
                onClick = { vm.save(onBack) },
                enabled = canSave && !state.saving,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber700)
            ) {
                Text(
                    if (state.isEditMode) "Update Entry" else "Save Entry",
                    fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
