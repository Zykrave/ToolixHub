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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.ToolixAmber
import kotlinx.coroutines.delay

@Composable
fun PomodoroScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var phaseIndex by remember { mutableIntStateOf(0) } // 0: Focus (25m), 1: Short Break (5m), 2: Long Break (15m)
    val phases = listOf("Focus" to 25 * 60, "Short Break" to 5 * 60, "Long Break" to 15 * 60)

    var currentDurationSec by remember { mutableIntStateOf(phases[0].second) }
    var remainingSec by remember { mutableIntStateOf(phases[0].second) }
    var isRunning by remember { mutableStateOf(false) }
    var completedPomodoros by remember { mutableIntStateOf(0) }

    fun switchPhase(idx: Int) {
        phaseIndex = idx
        isRunning = false
        currentDurationSec = phases[idx].second
        remainingSec = phases[idx].second
    }

    LaunchedEffect(isRunning, remainingSec) {
        if (isRunning && remainingSec > 0) {
            delay(1000)
            remainingSec--
            if (remainingSec == 0) {
                isRunning = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(500)
                }
                if (phaseIndex == 0) {
                    completedPomodoros++
                    if (completedPomodoros % 4 == 0) switchPhase(2) else switchPhase(1)
                } else {
                    switchPhase(0)
                }
            }
        }
    }

    val minutes = remainingSec / 60
    val seconds = remainingSec % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val progress = if (currentDurationSec > 0) remainingSec.toFloat() / currentDurationSec.toFloat() else 0f

    val phaseColor = when (phaseIndex) {
        0 -> MaterialTheme.colorScheme.primary
        1 -> Color(0xFF4CAF50)
        else -> ToolixAmber
    }

    ToolixToolScaffold(
        title = "Pomodoro Timer",
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
            TabRow(selectedTabIndex = phaseIndex, containerColor = MaterialTheme.colorScheme.surface) {
                phases.forEachIndexed { idx, (title, _) ->
                    Tab(
                        selected = phaseIndex == idx,
                        onClick = { switchPhase(idx) },
                        text = { Text(title) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Circular Timer Dial
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 10.dp,
                        color = phaseColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = phases[phaseIndex].first.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(color = phaseColor, fontWeight = FontWeight.Bold)
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
                        colors = ButtonDefaults.buttonColors(containerColor = phaseColor),
                        modifier = Modifier.weight(1f).height(52.dp).testTag("pomodoro_toggle_button")
                    ) {
                        Icon(imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(if (isRunning) "Pause" else "Start Focus")
                    }

                    OutlinedButton(
                        onClick = {
                            val nextIdx = (phaseIndex + 1) % 3
                            switchPhase(nextIdx)
                        },
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Skip")
                        Text(" Skip")
                    }
                }

                // Stats
                ToolixCard {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Pomodoro Sessions Completed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$completedPomodoros Focus Blocks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Cycle #${(completedPomodoros % 4) + 1}/4",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
