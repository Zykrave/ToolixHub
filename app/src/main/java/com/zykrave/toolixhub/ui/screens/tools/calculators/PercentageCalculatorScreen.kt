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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat

@Composable
fun PercentageCalculatorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("% of Value", "Ratio / Share", "Change %")

    ToolixToolScaffold(
        title = "Percentage Calculator",
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
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, maxLines = 1) },
                        modifier = Modifier.testTag("percentage_tab_$index")
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                when (selectedTab) {
                    0 -> ModePercentOf()
                    1 -> ModeRatio()
                    2 -> ModeChange()
                }
            }
        }
    }
}

@Composable
private fun ModePercentOf() {
    var percentInput by remember { mutableStateOf("15") }
    var valueInput by remember { mutableStateOf("250") }

    val percent = percentInput.toDoubleOrNull() ?: 0.0
    val value = valueInput.toDoubleOrNull() ?: 0.0
    val result = (percent / 100.0) * value
    val df = DecimalFormat("#,##0.##")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ToolixCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("What is X% of Y?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = percentInput,
                    onValueChange = { percentInput = it },
                    label = { Text("Percentage (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("percent_input_x")
                )
                OutlinedTextField(
                    value = valueInput,
                    onValueChange = { valueInput = it },
                    label = { Text("Total Value (Y)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("percent_input_y")
                )
            }
        }

        ResultCard(
            label = "Calculated Result",
            value = df.format(result),
            subtitle = "${df.format(percent)}% of ${df.format(value)} is ${df.format(result)}"
        )
    }
}

@Composable
private fun ModeRatio() {
    var partInput by remember { mutableStateOf("35") }
    var totalInput by remember { mutableStateOf("140") }

    val part = partInput.toDoubleOrNull() ?: 0.0
    val total = totalInput.toDoubleOrNull() ?: 1.0
    val percentage = if (total != 0.0) (part / total) * 100.0 else 0.0
    val df = DecimalFormat("#,##0.##")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ToolixCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("X is what percentage of Y?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = partInput,
                    onValueChange = { partInput = it },
                    label = { Text("Part (X)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = totalInput,
                    onValueChange = { totalInput = it },
                    label = { Text("Total (Y)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ResultCard(
            label = "Percentage Share",
            value = "${df.format(percentage)}%",
            subtitle = "${df.format(part)} is ${df.format(percentage)}% of ${df.format(total)}"
        )
    }
}

@Composable
private fun ModeChange() {
    var initialInput by remember { mutableStateOf("80") }
    var finalInput by remember { mutableStateOf("120") }

    val initial = initialInput.toDoubleOrNull() ?: 0.0
    val finalVal = finalInput.toDoubleOrNull() ?: 0.0
    val diff = finalVal - initial
    val percentChange = if (initial != 0.0) (diff / initial) * 100.0 else 0.0
    val df = DecimalFormat("#,##0.##")

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ToolixCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Percentage Increase / Decrease", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = initialInput,
                    onValueChange = { initialInput = it },
                    label = { Text("Initial Value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = finalInput,
                    onValueChange = { finalInput = it },
                    label = { Text("Final Value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        val prefix = if (percentChange > 0) "+" else ""
        val statusText = if (percentChange >= 0) "Increase of" else "Decrease of"

        ResultCard(
            label = "Percentage Change",
            value = "$prefix${df.format(percentChange)}%",
            subtitle = "$statusText ${df.format(kotlin.math.abs(diff))} (from ${df.format(initial)} to ${df.format(finalVal)})"
        )
    }
}
