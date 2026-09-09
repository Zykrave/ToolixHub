package com.zykrave.toolixhub.ui.screens.tools.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat
import kotlin.math.pow

@Composable
fun LoanEmiScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var principalInput by remember { mutableStateOf("250000") }
    var interestRateInput by remember { mutableStateOf("6.5") }
    var termYears by remember { mutableFloatStateOf(15f) }

    val principal = principalInput.toDoubleOrNull() ?: 0.0
    val annualRate = interestRateInput.toDoubleOrNull() ?: 0.0
    val months = (termYears * 12).toInt()

    val monthlyRate = (annualRate / 100.0) / 12.0
    val emi = if (monthlyRate > 0 && months > 0 && principal > 0) {
        val factor = (1 + monthlyRate).pow(months.toDouble())
        (principal * monthlyRate * factor) / (factor - 1)
    } else if (months > 0 && principal > 0) {
        principal / months
    } else 0.0

    val totalPayment = emi * months
    val totalInterest = (totalPayment - principal).coerceAtLeast(0.0)

    val df = DecimalFormat("$#,##0.00")

    ToolixToolScaffold(
        title = "Loan & EMI Calculator",
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Loan Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    OutlinedTextField(
                        value = principalInput,
                        onValueChange = { principalInput = it },
                        label = { Text("Loan Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("loan_principal_input")
                    )

                    OutlinedTextField(
                        value = interestRateInput,
                        onValueChange = { interestRateInput = it },
                        label = { Text("Annual Interest Rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Loan Tenure: ${termYears.toInt()} Years (${months} months)", fontWeight = FontWeight.Medium)
                    Slider(
                        value = termYears,
                        onValueChange = { termYears = it },
                        valueRange = 1f..30f,
                        steps = 28,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            ResultCard(
                label = "Monthly EMI",
                value = df.format(emi),
                subtitle = "Payable for ${termYears.toInt()} years ($months payments)"
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultCard(
                    label = "Total Interest",
                    value = df.format(totalInterest),
                    modifier = Modifier.weight(1f)
                )
                ResultCard(
                    label = "Total Payment",
                    value = df.format(totalPayment),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
