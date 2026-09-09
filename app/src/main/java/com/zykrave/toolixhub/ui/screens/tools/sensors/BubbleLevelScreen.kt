package com.zykrave.toolixhub.ui.screens.tools.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.sqrt

@Composable
fun BubbleLevelScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    var pitch by remember { mutableFloatStateOf(0f) }
    var roll by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val ax = event.values[0]
                    val ay = event.values[1]
                    val az = event.values[2]

                    // Calculate pitch and roll in degrees
                    val g = sqrt((ax * ax + ay * ay + az * az).toDouble())
                    if (g > 0) {
                        roll = Math.toDegrees(Math.asin((ax / g).toDouble().coerceIn(-1.0, 1.0))).toFloat()
                        pitch = Math.toDegrees(Math.asin((ay / g).toDouble().coerceIn(-1.0, 1.0))).toFloat()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accel?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val totalDeviation = sqrt((pitch * pitch + roll * roll).toDouble()).toFloat()
    val isLevel = totalDeviation < 0.8f
    val df = DecimalFormat("#0.0°")

    val levelColor by animateColorAsState(
        targetValue = if (isLevel) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
        label = "level_color"
    )

    ToolixToolScaffold(
        title = "Bubble Spirit Level",
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
            // Bullseye Target Canvas
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(3.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val outlineColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                val targetRadius = 120f

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    // Crosshairs
                    drawLine(outlineColor, Offset(0f, cy), Offset(size.width, cy), strokeWidth = 2f)
                    drawLine(outlineColor, Offset(cx, 0f), Offset(cx, size.height), strokeWidth = 2f)

                    // Target rings
                    drawCircle(outlineColor, radius = targetRadius * 0.25f, center = Offset(cx, cy), style = Stroke(2f))
                    drawCircle(outlineColor, radius = targetRadius * 0.5f, center = Offset(cx, cy), style = Stroke(2f))
                    drawCircle(outlineColor, radius = targetRadius * 0.8f, center = Offset(cx, cy), style = Stroke(2f))

                    // Center level zone circle
                    drawCircle(levelColor.copy(alpha = 0.25f), radius = 24f, center = Offset(cx, cy))
                    drawCircle(levelColor, radius = 24f, center = Offset(cx, cy), style = Stroke(3f))

                    // Bubble calculation: scale degrees to canvas offset
                    val maxOffset = targetRadius * 0.85f
                    val bx = (roll / 30f * maxOffset).coerceIn(-maxOffset, maxOffset)
                    val by = (pitch / 30f * maxOffset).coerceIn(-maxOffset, maxOffset)

                    // Draw moving spirit bubble
                    drawCircle(
                        color = levelColor,
                        radius = 20f,
                        center = Offset(cx - bx, cy + by)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isLevel) Color(0xFF4CAF50).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isLevel) Color(0xFF4CAF50) else Color.Transparent)
            ) {
                Text(
                    text = if (isLevel) "PERFECTLY LEVEL" else "TILTED (${df.format(totalDeviation)})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isLevel) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultCard(
                    label = "Pitch (Y-Axis)",
                    value = df.format(pitch),
                    subtitle = if (pitch > 0) "Tilted forward" else "Tilted back",
                    modifier = Modifier.weight(1f)
                )
                ResultCard(
                    label = "Roll (X-Axis)",
                    value = df.format(roll),
                    subtitle = if (roll > 0) "Tilted right" else "Tilted left",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
