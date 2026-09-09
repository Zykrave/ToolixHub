package com.zykrave.toolixhub.ui.screens.tools.timers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.ToolixAmber
import kotlinx.coroutines.delay

@Composable
fun CountdownTimerScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var totalDurationSec by remember { mutableIntStateOf(300) } // 5 mins default
    var remainingSec by remember { mutableIntStateOf(300) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, remainingSec) {
        if (isRunning && remainingSec > 0) {
            delay(1000)
            remainingSec--
            if (remainingSec == 0) {
                isRunning = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 300, 200, 500), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(500)
                }
            }
        }
    }

    val hours = remainingSec / 3600
    val minutes = (remainingSec % 3600) / 60
    val seconds = remainingSec % 60
    val timeFormatted = if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }

    val progress = if (totalDurationSec > 0) remainingSec.toFloat() / totalDurationSec.toFloat() else 0f

    val presets = listOf(
        "1m" to 60,
        "3m" to 180,
        "5m" to 300,
        "10m" to 600,
        "15m" to 900,
        "25m" to 1500
    )

    ToolixToolScaffold(
        title = "Countdown Timer",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Circular Countdown Display
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 10.dp,
                    color = if (remainingSec < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isRunning) "RUNNING" else if (remainingSec == 0) "TIME'S UP!" else "PAUSED",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (remainingSec == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
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
                        contentColor = if (isRunning) androidx.compose.ui.graphics.Color.Black else MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f).height(52.dp).testTag("timer_start_pause_button")
                ) {
                    Icon(imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(if (isRunning) "Pause" else "Start")
                }

                OutlinedButton(
                    onClick = {
                        isRunning = false
                        remainingSec = totalDurationSec
                    },
                    modifier = Modifier.height(52.dp).testTag("timer_reset_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                    Text(" Reset")
                }

                OutlinedButton(
                    onClick = {
                        remainingSec += 60
                        totalDurationSec += 60
                    },
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("+1 min")
                }
            }

            // Quick Presets
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Quick Presets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.take(3).forEach { (label, sec) ->
                            FilterChip(
                                selected = totalDurationSec == sec && !isRunning,
                                onClick = {
                                    isRunning = false
                                    totalDurationSec = sec
                                    remainingSec = sec
                                },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presets.drop(3).forEach { (label, sec) ->
                            FilterChip(
                                selected = totalDurationSec == sec && !isRunning,
                                onClick = {
                                    isRunning = false
                                    totalDurationSec = sec
                                    remainingSec = sec
                                },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
