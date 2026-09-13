package com.zykrave.toolixhub.ui.screens.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.zykrave.toolixhub.ui.components.ToolixDropdown
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat

data class CurrencyDef(val code: String, val name: String, val symbol: String, val defaultRateToUsd: Double)

@Composable
fun CurrencyConverterScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val currencies = remember {
        listOf(
            CurrencyDef("USD", "US Dollar", "$", 1.0),
            CurrencyDef("EUR", "Euro", "€", 1.08),
            CurrencyDef("GBP", "British Pound", "£", 1.28),
            CurrencyDef("JPY", "Japanese Yen", "¥", 0.0067),
            CurrencyDef("CAD", "Canadian Dollar", "C$", 0.74),
            CurrencyDef("AUD", "Australian Dollar", "A$", 0.66),
            CurrencyDef("CHF", "Swiss Franc", "CHF", 1.13),
            CurrencyDef("CNY", "Chinese Yuan", "¥", 0.14),
            CurrencyDef("INR", "Indian Rupee", "₹", 0.012),
            CurrencyDef("BRL", "Brazilian Real", "R$", 0.20),
            CurrencyDef("MXN", "Mexican Peso", "$", 0.059)
        )
    }

    var fromCurrency by remember { mutableStateOf(currencies[0]) } // USD
    var toCurrency by remember { mutableStateOf(currencies[1]) }   // EUR
    var amountInput by remember { mutableStateOf("100") }

    // Initial manual exchange rate (1 From = X To)
    val defaultRatio = fromCurrency.defaultRateToUsd / toCurrency.defaultRateToUsd
    var manualRateInput by remember(fromCurrency, toCurrency) {
        mutableStateOf(DecimalFormat("#.####").format(defaultRatio))
    }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val rate = manualRateInput.toDoubleOrNull() ?: 1.0
    val convertedAmount = amount * rate
    val df = DecimalFormat("#,##0.00")

    val currencyOptions = currencies.map { "${it.code} - ${it.name} (${it.symbol})" }

    ToolixToolScaffold(
        title = "Currency Converter",
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
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Offline Currency Converter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount to Convert") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("currency_amount_input")
                    )

                    // From Currency Dropdown
                    ToolixDropdown(
                        options = currencyOptions,
                        selectedOption = "${fromCurrency.code} - ${fromCurrency.name} (${fromCurrency.symbol})",
                        onOptionSelected = { selectedStr ->
                            val found = currencies.find { "${it.code} - ${it.name} (${it.symbol})" == selectedStr }
                            if (found != null) fromCurrency = found
                        },
                        label = "From Currency",
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Swap Currencies
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        FilledTonalIconButton(
                            onClick = {
                                val temp = fromCurrency
                                fromCurrency = toCurrency
                                toCurrency = temp
                            }
                        ) {
                            Icon(imageVector = Icons.Default.SwapVert, contentDescription = "Swap currencies")
                        }
                    }

                    // To Currency Dropdown
                    ToolixDropdown(
                        options = currencyOptions,
                        selectedOption = "${toCurrency.code} - ${toCurrency.name} (${toCurrency.symbol})",
                        onOptionSelected = { selectedStr ->
                            val found = currencies.find { "${it.code} - ${it.name} (${it.symbol})" == selectedStr }
                            if (found != null) toCurrency = found
                        },
                        label = "To Currency",
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Manual exchange rate field
                    OutlinedTextField(
                        value = manualRateInput,
                        onValueChange = { manualRateInput = it },
                        label = { Text("Exchange Rate (1 ${fromCurrency.code} = X ${toCurrency.code})") },
                        supportingText = { Text("Edit manually anytime for 100% offline accuracy") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("currency_rate_input")
                    )
                }
            }

            ResultCard(
                label = "Converted Total",
                value = "${toCurrency.symbol}${df.format(convertedAmount)} ${toCurrency.code}",
                subtitle = "${fromCurrency.symbol}${df.format(amount)} ${fromCurrency.code} at rate $manualRateInput"
            )
        }
    }
}
