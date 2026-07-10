package io.github.praveendeviam.cattlebook.ui.pin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
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
import io.github.praveendeviam.cattlebook.app
import io.github.praveendeviam.cattlebook.ui.theme.Green700

@Composable
fun PinEntryScreen(
    onUnlocked: () -> Unit,
    vm: PinViewModel = viewModel(factory = PinViewModel.factory(LocalContext.current.app.pinPreferences))
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.digits) {
        if (state.digits.length == 4) vm.verify(onUnlocked)
    }
    PinPad(
        title = "Enter PIN",
        digits = state.digits,
        error = state.error,
        onDigit = vm::onDigit,
        onDelete = vm::onDelete
    )
}

@Composable
fun PinSetupScreen(
    onDone: () -> Unit,
    vm: PinViewModel = viewModel(factory = PinViewModel.factory(LocalContext.current.app.pinPreferences))
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var confirming by remember { mutableStateOf(false) }
    var firstPin by remember { mutableStateOf("") }

    if (!confirming) {
        LaunchedEffect(state.digits) {
            if (state.digits.length == 4) {
                firstPin = state.digits
                confirming = true
                vm.reset()
            }
        }
        PinPad(title = "Set a PIN (4 digits)", digits = state.digits, error = false,
            onDigit = vm::onDigit, onDelete = vm::onDelete)
    } else {
        LaunchedEffect(state.digits) {
            if (state.digits.length == 4) {
                if (state.digits == firstPin) {
                    vm.setupPin(state.digits, onDone)
                } else {
                    confirming = false
                    firstPin = ""
                    vm.reset()
                }
            }
        }
        PinPad(title = "Confirm PIN", digits = state.digits,
            error = false, onDigit = vm::onDigit, onDelete = vm::onDelete)
    }
}

@Composable
private fun PinPad(
    title: String,
    digits: String,
    error: Boolean,
    onDigit: (String) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CattleBook", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Green700)
        Spacer(Modifier.height(32.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))

        // Dot indicators
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { i ->
                val filled = i < digits.length
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.size(20.dp),
                    color = if (filled) Green700 else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                ) {}
            }
        }

        if (error) {
            Spacer(Modifier.height(12.dp))
            Text("Wrong PIN. Try again.", color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(40.dp))

        // Number pad
        val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
        val rows = keys.chunked(3)
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                row.forEach { key ->
                    if (key == "⌫") {
                        FilledTonalIconButton(onClick = onDelete, modifier = Modifier.size(72.dp)) {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete")
                        }
                    } else if (key.isEmpty()) {
                        Spacer(Modifier.size(72.dp))
                    } else {
                        FilledTonalButton(
                            onClick = { onDigit(key) },
                            modifier = Modifier.size(72.dp),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(key, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
