package com.zykrave.toolixhub.ui.screens.tools.random

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RandomNumberScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var minInput by remember { mutableStateOf("1") }
    var maxInput by remember { mutableStateOf("100") }
    var count by remember { mutableIntStateOf(1) }
    var allowDuplicates by remember { mutableStateOf(false) }
    var sortAscending by remember { mutableStateOf(false) }

    val generatedNumbers = remember { mutableStateListOf(42) }
    val context = LocalContext.current

    fun generate() {
        val min = minInput.toIntOrNull() ?: 1
        val max = maxInput.toIntOrNull() ?: 100
        if (min > max) {
            Toast.makeText(context, "Minimum must be less than or equal to Maximum", Toast.LENGTH_SHORT).show()
            return
        }

        val rangeSize = (max - min + 1)
        if (!allowDuplicates && count > rangeSize) {
            Toast.makeText(context, "Range too small for $count unique numbers", Toast.LENGTH_SHORT).show()
            return
        }

        generatedNumbers.clear()
        val list = if (allowDuplicates) {
            (1..count).map { Random.nextInt(min, max + 1) }
        } else {
            val pool = (min..max).toMutableList()
            pool.shuffle()
            pool.take(count)
        }

        val finalList = if (sortAscending) list.sorted() else list
        generatedNumbers.addAll(finalList)
    }

    ToolixToolScaffold(
        title = "Random Number Generator",
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
                    Text("Range & Count Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = minInput,
                            onValueChange = { minInput = it },
                            label = { Text("Min") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("random_min_input")
                        )
                        OutlinedTextField(
                            value = maxInput,
                            onValueChange = { maxInput = it },
                            label = { Text("Max") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("random_max_input")
                        )
                    }

                    Text("Generate Count: $count", fontWeight = FontWeight.Medium)
                    Slider(
                        value = count.toFloat(),
                        onValueChange = { count = it.toInt() },
                        valueRange = 1f..30f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = !allowDuplicates, onCheckedChange = { allowDuplicates = !it })
                        Text("Unique Numbers Only (No duplicates)")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = sortAscending, onCheckedChange = { sortAscending = it })
                        Text("Sort in Ascending Order")
                    }

                    Button(
                        onClick = { generate() },
                        modifier = Modifier.fillMaxWidth().testTag("generate_random_button")
                    ) {
                        Text("Generate $count Random Numbers")
                    }
                }
            }

            // Results Display
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Generated Numbers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        generatedNumbers.forEach { num ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "$num",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val numbersStr = generatedNumbers.joinToString(", ")
            ResultCard(
                label = "Copy Numbers",
                value = numbersStr,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Random Numbers", numbersStr))
                    Toast.makeText(context, "Copied numbers to clipboard!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
