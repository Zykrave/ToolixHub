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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import kotlin.math.pow

@Composable
fun InterestCalculatorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var principalInput by remember { mutableStateOf("10000") }
    var rateInput by remember { mutableStateOf("7.0") }
    var years by remember { mutableFloatStateOf(5f) }
    var freqIdx by remember { mutableIntStateOf(1) } // 0: Simple, 1: Annually, 2: Quarterly, 3: Monthly

    val frequencies = listOf("Simple", "Annually", "Quarterly", "Monthly")
    val principal = principalInput.toDoubleOrNull() ?: 0.0
    val rate = rateInput.toDoubleOrNull() ?: 0.0
    val t = years.toDouble()

    val (finalAmount, interestEarned) = when (freqIdx) {
        0 -> {
            val i = principal * (rate / 100.0) * t
            (principal + i) to i
        }
        else -> {
            val n = when (freqIdx) {
                1 -> 1.0
                2 -> 4.0
                else -> 12.0
            }
            val a = principal * (1 + (rate / 100.0) / n).pow(n * t)
            a to (a - principal)
        }
    }

    val df = DecimalFormat("$#,##0.00")

    ToolixToolScaffold(
        title = "Interest Calculator",
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
                    Text("Investment Parameters", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    OutlinedTextField(
                        value = principalInput,
                        onValueChange = { principalInput = it },
                        label = { Text("Initial Principal ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = rateInput,
                        onValueChange = { rateInput = it },
                        label = { Text("Annual Rate (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Time Period: ${years.toInt()} Years", fontWeight = FontWeight.Medium)
                    Slider(
                        value = years,
                        onValueChange = { years = it },
                        valueRange = 1f..40f,
                        steps = 38,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Compounding Type", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        frequencies.forEachIndexed { index, label ->
                            FilterChip(
                                selected = freqIdx == index,
                                onClick = { freqIdx = index },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            ResultCard(
                label = "Maturity Total Amount",
                value = df.format(finalAmount),
                subtitle = "Principal of ${df.format(principal)} after ${years.toInt()} years"
            )

            ResultCard(
                label = "Total Interest Earned",
                value = df.format(interestEarned),
                subtitle = "${String.format("%.1f", if (principal > 0) (interestEarned / principal) * 100 else 0.0)}% total return"
            )
        }
    }
}
