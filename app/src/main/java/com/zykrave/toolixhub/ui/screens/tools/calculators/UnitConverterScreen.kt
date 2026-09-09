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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat

enum class UnitType(val displayName: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    VOLUME("Volume"),
    TEMPERATURE("Temperature"),
    AREA("Area"),
    SPEED("Speed")
}

data class UnitDef(val name: String, val symbol: String, val toBase: (Double) -> Double, val fromBase: (Double) -> Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = UnitType.values()
    val currentType = categories[selectedCategoryIndex]

    val unitsMap = remember {
        mapOf(
            UnitType.LENGTH to listOf(
                UnitDef("Meters", "m", { it }, { it }),
                UnitDef("Kilometers", "km", { it * 1000.0 }, { it / 1000.0 }),
                UnitDef("Centimeters", "cm", { it * 0.01 }, { it / 0.01 }),
                UnitDef("Millimeters", "mm", { it * 0.001 }, { it / 0.001 }),
                UnitDef("Inches", "in", { it * 0.0254 }, { it / 0.0254 }),
                UnitDef("Feet", "ft", { it * 0.3048 }, { it / 0.3048 }),
                UnitDef("Yards", "yd", { it * 0.9144 }, { it / 0.9144 }),
                UnitDef("Miles", "mi", { it * 1609.344 }, { it / 1609.344 })
            ),
            UnitType.WEIGHT to listOf(
                UnitDef("Kilograms", "kg", { it }, { it }),
                UnitDef("Grams", "g", { it * 0.001 }, { it / 0.001 }),
                UnitDef("Milligrams", "mg", { it * 0.000001 }, { it / 0.000001 }),
                UnitDef("Pounds", "lb", { it * 0.45359237 }, { it / 0.45359237 }),
                UnitDef("Ounces", "oz", { it * 0.02834952 }, { it / 0.02834952 }),
                UnitDef("Metric Tons", "t", { it * 1000.0 }, { it / 1000.0 })
            ),
            UnitType.VOLUME to listOf(
                UnitDef("Liters", "L", { it }, { it }),
                UnitDef("Milliliters", "mL", { it * 0.001 }, { it / 0.001 }),
                UnitDef("US Gallons", "gal", { it * 3.78541 }, { it / 3.78541 }),
                UnitDef("US Cups", "cup", { it * 0.236588 }, { it / 0.236588 }),
                UnitDef("US Fluid Ounces", "fl oz", { it * 0.0295735 }, { it / 0.0295735 }),
                UnitDef("Cubic Meters", "m³", { it * 1000.0 }, { it / 1000.0 })
            ),
            UnitType.TEMPERATURE to listOf(
                UnitDef("Celsius", "°C", { it }, { it }),
                UnitDef("Fahrenheit", "°F", { (it - 32.0) * 5.0 / 9.0 }, { it * 9.0 / 5.0 + 32.0 }),
                UnitDef("Kelvin", "K", { it - 273.15 }, { it + 273.15 })
            ),
            UnitType.AREA to listOf(
                UnitDef("Square Meters", "m²", { it }, { it }),
                UnitDef("Square Kilometers", "km²", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
                UnitDef("Square Feet", "ft²", { it * 0.092903 }, { it / 0.092903 }),
                UnitDef("Square Miles", "mi²", { it * 2_589_988.11 }, { it / 2_589_988.11 }),
                UnitDef("Acres", "ac", { it * 4046.86 }, { it / 4046.86 }),
                UnitDef("Hectares", "ha", { it * 10_000.0 }, { it / 10_000.0 })
            ),
            UnitType.SPEED to listOf(
                UnitDef("Kilometers/hour", "km/h", { it }, { it }),
                UnitDef("Miles/hour", "mph", { it * 1.60934 }, { it / 1.60934 }),
                UnitDef("Meters/second", "m/s", { it * 3.6 }, { it / 3.6 }),
                UnitDef("Knots", "kn", { it * 1.852 }, { it / 1.852 })
            )
        )
    }

    val currentUnits = unitsMap[currentType] ?: emptyList()
    var fromIndex by remember(currentType) { mutableIntStateOf(0) }
    var toIndex by remember(currentType) { mutableIntStateOf(if (currentUnits.size > 1) 1 else 0) }
    var inputValue by remember { mutableStateOf("1") }

    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val fromUnit = currentUnits.getOrElse(fromIndex) { currentUnits[0] }
    val toUnit = currentUnits.getOrElse(toIndex) { currentUnits[0] }

    val inputNum = inputValue.toDoubleOrNull() ?: 0.0
    val baseVal = fromUnit.toBase(inputNum)
    val convertedVal = toUnit.fromBase(baseVal)
    val df = DecimalFormat("#,##0.######")

    ToolixToolScaffold(
        title = "Unit Converter",
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
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 16.dp
            ) {
                categories.forEachIndexed { idx, cat ->
                    Tab(
                        selected = selectedCategoryIndex == idx,
                        onClick = {
                            selectedCategoryIndex = idx
                            fromIndex = 0
                            toIndex = 1
                        },
                        text = { Text(cat.displayName) }
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ToolixCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = inputValue,
                            onValueChange = { inputValue = it },
                            label = { Text("Input Value") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth().testTag("unit_input_value")
                        )

                        // From dropdown
                        ExposedDropdownMenuBox(
                            expanded = fromExpanded,
                            onExpandedChange = { fromExpanded = !fromExpanded }
                        ) {
                            OutlinedTextField(
                                value = "${fromUnit.name} (${fromUnit.symbol})",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("From") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = fromExpanded,
                                onDismissRequest = { fromExpanded = false }
                            ) {
                                currentUnits.forEachIndexed { i, unit ->
                                    DropdownMenuItem(
                                        text = { Text("${unit.name} (${unit.symbol})") },
                                        onClick = {
                                            fromIndex = i
                                            fromExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Swap Button
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            FilledTonalIconButton(
                                onClick = {
                                    val tmp = fromIndex
                                    fromIndex = toIndex
                                    toIndex = tmp
                                }
                            ) {
                                Icon(imageVector = Icons.Default.SwapVert, contentDescription = "Swap units")
                            }
                        }

                        // To dropdown
                        ExposedDropdownMenuBox(
                            expanded = toExpanded,
                            onExpandedChange = { toExpanded = !toExpanded }
                        ) {
                            OutlinedTextField(
                                value = "${toUnit.name} (${toUnit.symbol})",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("To") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = toExpanded,
                                onDismissRequest = { toExpanded = false }
                            ) {
                                currentUnits.forEachIndexed { i, unit ->
                                    DropdownMenuItem(
                                        text = { Text("${unit.name} (${unit.symbol})") },
                                        onClick = {
                                            toIndex = i
                                            toExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                ResultCard(
                    label = "Converted Result",
                    value = "${df.format(convertedVal)} ${toUnit.symbol}",
                    subtitle = "$inputValue ${fromUnit.symbol} = ${df.format(convertedVal)} ${toUnit.symbol}"
                )
            }
        }
    }
}
