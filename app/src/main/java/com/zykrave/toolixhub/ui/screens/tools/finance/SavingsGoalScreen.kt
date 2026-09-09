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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat

@Composable
fun SavingsGoalScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var goalInput by remember { mutableStateOf("50000") }
    var initialInput by remember { mutableStateOf("5000") }
    var monthlyInput by remember { mutableStateOf("600") }
    var returnRateInput by remember { mutableStateOf("7.0") }

    val goal = goalInput.toDoubleOrNull() ?: 0.0
    val initial = initialInput.toDoubleOrNull() ?: 0.0
    val monthly = monthlyInput.toDoubleOrNull() ?: 0.0
    val annualReturn = returnRateInput.toDoubleOrNull() ?: 0.0
    val monthlyRate = (annualReturn / 100.0) / 12.0

    // Simulate month by month
    var currentBalance = initial
    var totalMonths = 0
    val maxMonths = 1200 // 100 years cutoff

    while (currentBalance < goal && totalMonths < maxMonths && (monthly > 0 || monthlyRate > 0)) {
        currentBalance += monthly
        currentBalance += currentBalance * monthlyRate
        totalMonths++
    }

    val years = totalMonths / 12
    val remainderMonths = totalMonths % 12
    val totalDeposited = initial + (monthly * totalMonths)
    val interestEarned = (currentBalance - totalDeposited).coerceAtLeast(0.0)

    val df = DecimalFormat("$#,##0.00")

    ToolixToolScaffold(
        title = "Savings Goal Planner",
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
                    Text("Goal & Contribution Targets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it },
                        label = { Text("Target Goal Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = initialInput,
                        onValueChange = { initialInput = it },
                        label = { Text("Initial Deposit ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = monthlyInput,
                        onValueChange = { monthlyInput = it },
                        label = { Text("Monthly Contribution ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = returnRateInput,
                        onValueChange = { returnRateInput = it },
                        label = { Text("Expected Annual Return (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            val timeToGoal = if (totalMonths >= maxMonths) "Over 100 years" else "$years years and $remainderMonths months ($totalMonths months)"

            ResultCard(
                label = "Time to Reach Goal",
                value = timeToGoal,
                subtitle = "To reach ${df.format(goal)} target balance"
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultCard(
                    label = "Total Deposited",
                    value = df.format(totalDeposited),
                    modifier = Modifier.weight(1f)
                )
                ResultCard(
                    label = "Interest Earned",
                    value = df.format(interestEarned),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
