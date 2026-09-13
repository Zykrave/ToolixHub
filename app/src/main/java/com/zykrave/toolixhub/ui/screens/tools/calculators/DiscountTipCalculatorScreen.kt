package com.zykrave.toolixhub.ui.screens.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.zykrave.toolixhub.ui.components.ToolixFilterChip
import com.zykrave.toolixhub.ui.components.ToolixSlider
import com.zykrave.toolixhub.ui.components.ToolixTabRow
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat

@Composable
fun DiscountTipCalculatorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Discount", "Tip & Split")

    ToolixToolScaffold(
        title = "Discount & Tip Calculator",
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ToolixTabRow(
                tabs = tabs,
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                scrollable = false
            )

            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedTab == 0) {
                    DiscountView()
                } else {
                    TipSplitView()
                }
            }
        }
    }
}

@Composable
private fun DiscountView() {
    var priceInput by remember { mutableStateOf("120") }
    var discountInput by remember { mutableStateOf("25") }
    var taxInput by remember { mutableStateOf("8.5") }

    val originalPrice = priceInput.toDoubleOrNull() ?: 0.0
    val discountPercent = discountInput.toDoubleOrNull() ?: 0.0
    val taxPercent = taxInput.toDoubleOrNull() ?: 0.0

    val discountAmount = originalPrice * (discountPercent / 100.0)
    val discountedPrice = (originalPrice - discountAmount).coerceAtLeast(0.0)
    val taxAmount = discountedPrice * (taxPercent / 100.0)
    val finalPrice = discountedPrice + taxAmount

    val df = DecimalFormat("$#,##0.00")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ToolixCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Sale & Discount Calculation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    label = { Text("Original Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("original_price_input")
                )
                OutlinedTextField(
                    value = discountInput,
                    onValueChange = { discountInput = it },
                    label = { Text("Discount (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = taxInput,
                    onValueChange = { taxInput = it },
                    label = { Text("Sales Tax (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ResultCard(
            label = "Final Price to Pay",
            value = df.format(finalPrice),
            subtitle = "You save ${df.format(discountAmount)} (${discountPercent}%) with ${df.format(taxAmount)} tax"
        )
    }
}

@Composable
private fun TipSplitView() {
    var billInput by remember { mutableStateOf("85.00") }
    var tipPercent by remember { mutableStateOf(18) }
    var peopleCount by remember { mutableStateOf(3) }

    val bill = billInput.toDoubleOrNull() ?: 0.0
    val tipAmount = bill * (tipPercent / 100.0)
    val totalBill = bill + tipAmount
    val perPerson = if (peopleCount > 0) totalBill / peopleCount else totalBill

    val df = DecimalFormat("$#,##0.00")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ToolixCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Bill & Tip Splitter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = billInput,
                    onValueChange = { billInput = it },
                    label = { Text("Bill Total ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("bill_amount_input")
                )

                Text("Tip: $tipPercent%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 15, 18, 20, 25).forEach { pct ->
                        ToolixFilterChip(
                            selected = tipPercent == pct,
                            onClick = { tipPercent = pct },
                            label = "$pct%"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Split Between: $peopleCount people", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                ToolixSlider(
                    value = peopleCount.toFloat(),
                    onValueChange = { peopleCount = it.toInt() },
                    valueRange = 1f..20f,
                    steps = 18,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ResultCard(
            label = "Total Per Person",
            value = df.format(perPerson),
            subtitle = "Total Bill: ${df.format(totalBill)} (Tip: ${df.format(tipAmount)})"
        )
    }
}
