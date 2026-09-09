package com.zykrave.toolixhub.ui.screens.tools.random

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.ToolixAmber
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun DecisionSpinnerScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var optionsInput by remember {
        mutableStateOf("Pizza\nTacos\nSushi\nBurgers\nSalad\nThai Food")
    }
    var decision by remember { mutableStateOf("Sushi") }

    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }
    val scope = rememberCoroutineScope()
    val rotationAnim = remember { Animatable(0f) }

    fun triggerVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(40)
        }
    }

    fun makeDecision() {
        val items = optionsInput.split("\n").map { it.trim() }.filter { it.isNotBlank() }
        if (items.isNotEmpty()) {
            triggerVibrate()
            val chosen = items.random()
            scope.launch {
                rotationAnim.snapTo(0f)
                val targetSpin = 1080f + Random.nextInt(0, 360)
                rotationAnim.animateTo(targetSpin, animationSpec = tween(700))
                decision = chosen
            }
        }
    }

    ToolixToolScaffold(
        title = "Decision Spinner",
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
            // Visual rotating wheel
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                val primary = MaterialTheme.colorScheme.primary
                val secondary = MaterialTheme.colorScheme.secondary
                val amber = ToolixAmber
                val bg = MaterialTheme.colorScheme.surfaceVariant

                Canvas(
                    modifier = Modifier
                        .size(160.dp)
                        .rotate(rotationAnim.value)
                ) {
                    val colors = listOf(primary, secondary, amber, bg)
                    val sweepAngle = 360f / 6
                    for (i in 0 until 6) {
                        drawArc(
                            color = colors[i % colors.size],
                            startAngle = i * sweepAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true
                        )
                    }
                }

                // Pointer
                Canvas(modifier = Modifier.size(24.dp)) {
                    val path = Path().apply {
                        moveTo(size.width / 2f, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(path, color = Color.White)
                }
            }

            ResultCard(
                label = "The Decision",
                value = decision,
                subtitle = "Randomly selected from your options",
                accentColor = ToolixAmber
            )

            Button(
                onClick = { makeDecision() },
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("spin_decision_button")
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                Text(" Spin / Pick Choice", fontWeight = FontWeight.Bold)
            }

            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Options List (One per line)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = optionsInput,
                        onValueChange = { optionsInput = it },
                        modifier = Modifier.fillMaxWidth().height(160.dp).testTag("decision_options_input")
                    )
                }
            }
        }
    }
}
