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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeCalculatorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    // Default birthdate: Jan 1, 2000
    val initialCal = Calendar.getInstance().apply {
        set(2000, Calendar.JANUARY, 1)
    }
    var birthDateMillis by remember { mutableLongStateOf(initialCal.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthDateMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { birthDateMillis = it }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val birthCal = Calendar.getInstance().apply { timeInMillis = birthDateMillis }
    val nowCal = Calendar.getInstance()

    var years = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
    var months = nowCal.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH)
    var days = nowCal.get(Calendar.DAY_OF_MONTH) - birthCal.get(Calendar.DAY_OF_MONTH)

    if (days < 0) {
        months--
        val prevMonthCal = Calendar.getInstance().apply {
            timeInMillis = nowCal.timeInMillis
            add(Calendar.MONTH, -1)
        }
        days += prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    if (months < 0) {
        years--
        months += 12
    }

    val diffMillis = (nowCal.timeInMillis - birthCal.timeInMillis).coerceAtLeast(0L)
    val totalDays = diffMillis / (1000 * 60 * 60 * 24)
    val totalHours = diffMillis / (1000 * 60 * 60)

    // Next birthday calculation
    val nextBdayCal = Calendar.getInstance().apply {
        set(Calendar.MONTH, birthCal.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, birthCal.get(Calendar.DAY_OF_MONTH))
        if (before(nowCal)) {
            add(Calendar.YEAR, 1)
        }
    }
    val daysToBday = ((nextBdayCal.timeInMillis - nowCal.timeInMillis) / (1000 * 60 * 60 * 24)).coerceAtLeast(0L)

    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

    ToolixToolScaffold(
        title = "Age Calculator",
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
                    Text("Date of Birth", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "Selected: ${dateFormat.format(Date(birthDateMillis))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Select Date of Birth")
                    }
                }
            }

            ResultCard(
                label = "Exact Age",
                value = "$years years, $months months, $days days",
                subtitle = "Total Days Lived: $totalDays days (~$totalHours hours)"
            )

            ResultCard(
                label = "Next Birthday",
                value = "$daysToBday days remaining",
                subtitle = "Birthday on ${SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(nextBdayCal.time)}"
            )
        }
    }
}
