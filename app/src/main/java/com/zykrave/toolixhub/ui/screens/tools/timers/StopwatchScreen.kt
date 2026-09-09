package com.zykrave.toolixhub.ui.screens.tools.timers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.ToolixAmber
import kotlinx.coroutines.delay

data class LapRecord(val lapNumber: Int, val lapTimeMillis: Long, val totalTimeMillis: Long)

@Composable
fun StopwatchScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    var elapsedMillis by remember { mutableLongStateOf(0L) }
    var lastLapTotalMillis by remember { mutableLongStateOf(0L) }

    val laps = remember { mutableStateListOf<LapRecord>() }

    LaunchedEffect(isRunning) {
        val startTime = System.currentTimeMillis() - elapsedMillis
        while (isRunning) {
            elapsedMillis = System.currentTimeMillis() - startTime
            delay(30)
        }
    }

    fun formatTime(ms: Long): String {
        val minutes = ms / (1000 * 60)
        val seconds = (ms / 1000) % 60
        val centis = (ms % 1000) / 10
        return String.format("%02d:%02d.%02d", minutes, seconds, centis)
    }

    fun recordLap() {
        val lapTime = elapsedMillis - lastLapTotalMillis
        lastLapTotalMillis = elapsedMillis
        laps.add(0, LapRecord(laps.size + 1, lapTime, elapsedMillis))
    }

    val bestLapMillis = laps.minOfOrNull { it.lapTimeMillis }
    val worstLapMillis = if (laps.size > 1) laps.maxOfOrNull { it.lapTimeMillis } else null

    ToolixToolScaffold(
        title = "Stopwatch",
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big Display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatTime(elapsedMillis),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { isRunning = !isRunning },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) ToolixAmber else MaterialTheme.colorScheme.primary,
                        contentColor = if (isRunning) Color.Black else MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f).height(52.dp).testTag("stopwatch_toggle_button")
                ) {
                    Icon(imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(if (isRunning) "Stop" else "Start", fontWeight = FontWeight.Bold)
                }

                if (isRunning) {
                    Button(
                        onClick = { recordLap() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f).height(52.dp).testTag("stopwatch_lap_button")
                    ) {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = null)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Lap")
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            elapsedMillis = 0L
                            lastLapTotalMillis = 0L
                            laps.clear()
                        },
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Reset")
                    }
                }
            }

            // Laps List
            ToolixCard(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Text("Lap Times", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (laps.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No laps recorded yet", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(laps) { _, record ->
                                val isBest = record.lapTimeMillis == bestLapMillis && laps.size > 1
                                val isWorst = record.lapTimeMillis == worstLapMillis && laps.size > 1

                                val tagColor = when {
                                    isBest -> Color(0xFF4CAF50)
                                    isWorst -> Color(0xFFFF5252)
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Lap #${record.lapNumber}", fontWeight = FontWeight.Medium)
                                        Text("+${formatTime(record.lapTimeMillis)}", color = tagColor, fontWeight = FontWeight.Bold)
                                        Text(formatTime(record.totalTimeMillis), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
