package com.zykrave.toolixhub.ui.screens.tools.timers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.ToolixAmber
import kotlinx.coroutines.delay

enum class IntervalPhase {
    IDLE,
    WORK,
    REST,
    FINISHED
}

@Composable
fun IntervalTrainerScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var workSec by remember { mutableIntStateOf(30) }
    var restSec by remember { mutableIntStateOf(15) }
    var totalRounds by remember { mutableIntStateOf(6) }

    var currentRound by remember { mutableIntStateOf(1) }
    var currentPhase by remember { mutableStateOf(IntervalPhase.IDLE) }
    var secondsRemaining by remember { mutableIntStateOf(workSec) }
    var isRunning by remember { mutableStateOf(false) }

    fun triggerTransitionVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 300), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(300)
        }
    }

    LaunchedEffect(isRunning, secondsRemaining) {
        if (isRunning && secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
            if (secondsRemaining == 0) {
                triggerTransitionVibrate()
                when (currentPhase) {
                    IntervalPhase.WORK -> {
                        if (currentRound >= totalRounds) {
                            currentPhase = IntervalPhase.FINISHED
                            isRunning = false
                        } else {
                            currentPhase = IntervalPhase.REST
                            secondsRemaining = restSec
                        }
                    }
                    IntervalPhase.REST -> {
                        currentRound++
                        currentPhase = IntervalPhase.WORK
                        secondsRemaining = workSec
                    }
                    else -> {}
                }
            }
        }
    }

    val phaseColor by animateColorAsState(
        targetValue = when (currentPhase) {
            IntervalPhase.WORK -> Color(0xFF00E676)
            IntervalPhase.REST -> ToolixAmber
            IntervalPhase.FINISHED -> MaterialTheme.colorScheme.primary
            IntervalPhase.IDLE -> MaterialTheme.colorScheme.primary
        },
        label = "phase_color"
    )

    ToolixToolScaffold(
        title = "Interval & HIIT Timer",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Big Display Banner
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = phaseColor.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(2.dp, phaseColor),
                modifier = Modifier.fillMaxWidth().height(180.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when (currentPhase) {
                            IntervalPhase.WORK -> "WORK 🔥"
                            IntervalPhase.REST -> "REST 💨"
                            IntervalPhase.FINISHED -> "WORKOUT COMPLETE! 🏆"
                            IntervalPhase.IDLE -> "READY"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = phaseColor)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (currentPhase == IntervalPhase.FINISHED) "00" else String.format("%02d", secondsRemaining),
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Round $currentRound of $totalRounds",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (currentPhase == IntervalPhase.IDLE || currentPhase == IntervalPhase.FINISHED) {
                            currentRound = 1
                            currentPhase = IntervalPhase.WORK
                            secondsRemaining = workSec
                            isRunning = true
                        } else {
                            isRunning = !isRunning
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = phaseColor),
                    modifier = Modifier.weight(1f).height(52.dp).testTag("interval_start_button")
                ) {
                    Icon(imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(if (isRunning) "Pause" else "Start Workout")
                }

                OutlinedButton(
                    onClick = {
                        isRunning = false
                        currentPhase = IntervalPhase.IDLE
                        currentRound = 1
                        secondsRemaining = workSec
                    },
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                    Text(" Reset")
                }
            }

            // Setup Options
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Interval Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Text("Work Time: ${workSec}s", fontWeight = FontWeight.Medium)
                    Slider(
                        value = workSec.toFloat(),
                        onValueChange = {
                            workSec = it.toInt()
                            if (currentPhase == IntervalPhase.IDLE) secondsRemaining = workSec
                        },
                        valueRange = 10f..120f,
                        steps = 21,
                        enabled = !isRunning,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Rest Time: ${restSec}s", fontWeight = FontWeight.Medium)
                    Slider(
                        value = restSec.toFloat(),
                        onValueChange = { restSec = it.toInt() },
                        valueRange = 5f..60f,
                        steps = 10,
                        enabled = !isRunning,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Total Rounds: $totalRounds", fontWeight = FontWeight.Medium)
                    Slider(
                        value = totalRounds.toFloat(),
                        onValueChange = { totalRounds = it.toInt() },
                        valueRange = 2f..20f,
                        steps = 17,
                        enabled = !isRunning,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
