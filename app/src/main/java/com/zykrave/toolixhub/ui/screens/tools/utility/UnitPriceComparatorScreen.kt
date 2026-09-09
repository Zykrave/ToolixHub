package com.zykrave.toolixhub.ui.screens.tools.utility

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat

@Composable
fun UnitPriceComparatorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var priceA by remember { mutableStateOf("4.99") }
    var qtyA by remember { mutableStateOf("500") }

    var priceB by remember { mutableStateOf("7.49") }
    var qtyB by remember { mutableStateOf("800") }

    val pA = priceA.toDoubleOrNull() ?: 0.0
    val qA = qtyA.toDoubleOrNull() ?: 1.0

    val pB = priceB.toDoubleOrNull() ?: 0.0
    val qB = qtyB.toDoubleOrNull() ?: 1.0

    val unitPriceA = if (qA > 0) pA / qA else 0.0
    val unitPriceB = if (qB > 0) pB / qB else 0.0

    val df = DecimalFormat("$#,##0.0000")

    val verdict = when {
        unitPriceA == 0.0 || unitPriceB == 0.0 -> "Enter prices and quantities"
        unitPriceA < unitPriceB -> {
            val diffPct = ((unitPriceB - unitPriceA) / unitPriceB) * 100
            "Product A is ${String.format("%.1f", diffPct)}% cheaper!"
        }
        unitPriceB < unitPriceA -> {
            val diffPct = ((unitPriceA - unitPriceB) / unitPriceA) * 100
            "Product B is ${String.format("%.1f", diffPct)}% cheaper!"
        }
        else -> "Both products have identical unit price"
    }

    val verdictColor = when {
        unitPriceA < unitPriceB || unitPriceB < unitPriceA -> Color(0xFF4CAF50)
        else -> MaterialTheme.colorScheme.primary
    }

    ToolixToolScaffold(
        title = "Unit Price Comparator",
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
            ResultCard(
                label = "Value Verdict",
                value = verdict,
                subtitle = "Product A: ${df.format(unitPriceA)}/unit vs Product B: ${df.format(unitPriceB)}/unit",
                accentColor = verdictColor
            )

            // Product A Card
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Product A (Option 1)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = priceA,
                            onValueChange = { priceA = it },
                            label = { Text("Price ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = qtyA,
                            onValueChange = { qtyA = it },
                            label = { Text("Quantity / Grams") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Unit Cost: ${df.format(unitPriceA)} per unit", fontWeight = FontWeight.SemiBold)
                }
            }

            // Product B Card
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Product B (Option 2)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = priceB,
                            onValueChange = { priceB = it },
                            label = { Text("Price ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = qtyB,
                            onValueChange = { qtyB = it },
                            label = { Text("Quantity / Grams") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text("Unit Cost: ${df.format(unitPriceB)} per unit", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
