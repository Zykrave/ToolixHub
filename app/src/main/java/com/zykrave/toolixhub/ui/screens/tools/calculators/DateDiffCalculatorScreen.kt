package com.zykrave.toolixhub.ui.screens.tools.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDiffCalculatorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val now = Calendar.getInstance()
    var startDateMillis by remember { mutableLongStateOf(now.timeInMillis) }

    val nextMonth = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }
    var endDateMillis by remember { mutableLongStateOf(nextMonth.timeInMillis) }

    var pickingStart by remember { mutableStateOf<Boolean?>(null) }

    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (pickingStart == true) startDateMillis else endDateMillis
    )

    if (pickingStart != null) {
        DatePickerDialog(
            onDismissRequest = { pickingStart = null },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let {
                            if (pickingStart == true) startDateMillis = it else endDateMillis = it
                        }
                        pickingStart = null
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { pickingStart = null }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    val diffMillis = abs(endDateMillis - startDateMillis)
    val totalDays = diffMillis / (1000 * 60 * 60 * 24)
    val totalWeeks = totalDays / 7
    val remainderDays = totalDays % 7

    val cal1 = Calendar.getInstance().apply { timeInMillis = minOf(startDateMillis, endDateMillis) }
    val cal2 = Calendar.getInstance().apply { timeInMillis = maxOf(startDateMillis, endDateMillis) }

    var y = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)
    var m = cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH)
    var d = cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH)

    if (d < 0) {
        m--
        val prev = Calendar.getInstance().apply {
            timeInMillis = cal2.timeInMillis
            add(Calendar.MONTH, -1)
        }
        d += prev.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    if (m < 0) {
        y--
        m += 12
    }

    val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())

    ToolixToolScaffold(
        title = "Date Difference",
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
                    Text("Selected Dates", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Text("Start: ${dateFormat.format(Date(startDateMillis))}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(
                        onClick = { pickingStart = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Change Start Date")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("End: ${dateFormat.format(Date(endDateMillis))}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(
                        onClick = { pickingStart = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Change End Date")
                    }
                }
            }

            ResultCard(
                label = "Calendar Span",
                value = "$y years, $m months, $d days",
                subtitle = "Exact difference between dates"
            )

            ResultCard(
                label = "Total Duration",
                value = "$totalDays Days",
                subtitle = "$totalWeeks weeks and $remainderDays days"
            )
        }
    }
}
