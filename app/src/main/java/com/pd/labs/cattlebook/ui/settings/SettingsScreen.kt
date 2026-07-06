package com.pd.labs.cattlebook.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pd.labs.cattlebook.app
import com.pd.labs.cattlebook.ui.pin.PinViewModel
import com.pd.labs.cattlebook.ui.theme.Green700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSetupPin: () -> Unit,
    vm: PinViewModel = viewModel(factory = PinViewModel.factory(LocalContext.current.app.pinPreferences))
) {
    val pinEnabled by vm.pinEnabled.collectAsStateWithLifecycle(false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
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
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (pinEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = Green700,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("PIN Lock", style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (pinEnabled) "App is locked with a PIN" else "No PIN set",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    if (pinEnabled) {
                        TextButton(onClick = { vm.disablePin(onBack) }) { Text("Remove") }
                    } else {
                        TextButton(onClick = onSetupPin) { Text("Set PIN") }
                    }
                }
            }
        }
    }
}
