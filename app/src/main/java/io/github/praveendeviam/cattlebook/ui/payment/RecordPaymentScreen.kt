package io.github.praveendeviam.cattlebook.ui.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.praveendeviam.cattlebook.app
import io.github.praveendeviam.cattlebook.ui.common.DateField
import io.github.praveendeviam.cattlebook.ui.theme.Amber700
import io.github.praveendeviam.cattlebook.ui.theme.Green600
import io.github.praveendeviam.cattlebook.ui.theme.Green700
import io.github.praveendeviam.cattlebook.ui.theme.Green800
import io.github.praveendeviam.cattlebook.ui.theme.Teal700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentScreen(
    onBack: () -> Unit,
    vm: RecordPaymentViewModel = viewModel(
        factory = RecordPaymentViewModel.factory(LocalContext.current.app.repository)
    )
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Payment", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Amber700,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepHeader(step = 1, title = "Select date range", color = Green700)

            DateField(
                label = "From Date",
                date = state.startDate,
                onDateSelected = vm::onStartDateChange,
                modifier = Modifier.fillMaxWidth()
            )
            DateField(
                label = "To Date",
                date = state.endDate,
                onDateSelected = vm::onEndDateChange,
                modifier = Modifier.fillMaxWidth()
            )

            // Gradient milk total card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Green800, Green600),
                            start = Offset.Zero,
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Milk collected in range",
                            color = Color.White.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${"%.1f".format(state.totalLitres)} L",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            StepHeader(step = 2, title = "Enter rate & amount", color = Amber700)

            OutlinedTextField(
                value = state.ratePerLitre,
                onValueChange = vm::onRateChange,
                label = { Text("Rate per Litre (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true,
                supportingText = if (state.ratePerLitre.isNotBlank() && state.ratePerLitre.toDoubleOrNull() != null)
                    {{ Text("${"%.1f".format(state.totalLitres)} L × ₹${state.ratePerLitre}/L") }}
                else null
            )

            OutlinedTextField(
                value = state.amount,
                onValueChange = vm::onAmountChange,
                label = { Text("Amount Received (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 22.sp),
                singleLine = true
            )

            Spacer(Modifier.height(4.dp))
            StepHeader(step = 3, title = "Payment date", color = Teal700)

            DateField(
                label = "Payment Date",
                date = state.paymentDate,
                onDateSelected = vm::onPaymentDateChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { vm.save(onBack) },
                enabled = state.amount.toDoubleOrNull() != null && !state.saving,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Amber700),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Confirm & Close Period", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                "This will close the current period and start a new one.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun StepHeader(step: Int, title: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$step",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
