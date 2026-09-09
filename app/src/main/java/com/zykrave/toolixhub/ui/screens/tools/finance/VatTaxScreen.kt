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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun VatTaxScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var mode by remember { mutableIntStateOf(0) } // 0: Add Tax (Net -> Gross), 1: Remove Tax (Gross -> Net)
    var amountInput by remember { mutableStateOf("100") }
    var taxRateInput by remember { mutableStateOf("20") }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val taxRate = taxRateInput.toDoubleOrNull() ?: 0.0

    val (net, tax, gross) = if (mode == 0) {
        val t = amount * (taxRate / 100.0)
        Triple(amount, t, amount + t)
    } else {
        val n = amount / (1 + (taxRate / 100.0))
        Triple(n, amount - n, amount)
    }

    val df = DecimalFormat("$#,##0.00")

    ToolixToolScaffold(
        title = "VAT / Tax Calculator",
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
            TabRow(
                selectedTabIndex = mode,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(selected = mode == 0, onClick = { mode = 0 }, text = { Text("Add Tax (Net → Gross)") })
                Tab(selected = mode == 1, onClick = { mode = 1 }, text = { Text("Remove Tax (Gross → Net)") })
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ToolixCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = if (mode == 0) "Net Amount (Before Tax)" else "Gross Amount (Including Tax)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = { amountInput = it },
                            label = { Text("Amount ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = taxRateInput,
                            onValueChange = { taxRateInput = it },
                            label = { Text("Tax Rate (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(5, 10, 15, 20, 21).forEach { rate ->
                                FilterChip(
                                    selected = taxRateInput == rate.toString(),
                                    onClick = { taxRateInput = rate.toString() },
                                    label = { Text("$rate%") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                ResultCard(
                    label = if (mode == 0) "Gross Total (To Pay)" else "Net Amount (Base Price)",
                    value = if (mode == 0) df.format(gross) else df.format(net),
                    subtitle = "Tax portion: ${df.format(tax)} ($taxRate%)"
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResultCard(
                        label = "Net (Excl. Tax)",
                        value = df.format(net),
                        modifier = Modifier.weight(1f)
                    )
                    ResultCard(
                        label = "Tax Amount",
                        value = df.format(tax),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
