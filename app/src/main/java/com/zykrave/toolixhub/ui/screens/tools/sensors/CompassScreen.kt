package com.zykrave.toolixhub.ui.screens.tools.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.ToolixAmber

@Composable
fun CompassScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    var azimuthDegrees by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val rotSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        var gravity: FloatArray? = null
        var geomagnetic: FloatArray? = null

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotMatrix, orientation)
                    val deg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    azimuthDegrees = (deg + 360f) % 360f
                } else {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        gravity = event.values.clone()
                    }
                    if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        geomagnetic = event.values.clone()
                    }
                    if (gravity != null && geomagnetic != null) {
                        val r = FloatArray(9)
                        val i = FloatArray(9)
                        if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                            val orientation = FloatArray(3)
                            SensorManager.getOrientation(r, orientation)
                            val deg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                            azimuthDegrees = (deg + 360f) % 360f
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotSensor != null) {
            sensorManager.registerListener(listener, rotSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            magSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val cardinal = when (azimuthDegrees.toInt()) {
        in 338..360, in 0..22 -> "North (N)"
        in 23..67 -> "North-East (NE)"
        in 68..112 -> "East (E)"
        in 113..157 -> "South-East (SE)"
        in 158..202 -> "South (S)"
        in 203..247 -> "South-West (SW)"
        in 248..292 -> "West (W)"
        else -> "North-West (NW)"
    }

    val animatedRotation by animateFloatAsState(
        targetValue = -azimuthDegrees,
        animationSpec = spring(stiffness = 500f),
        label = "compass_rotation"
    )

    ToolixToolScaffold(
        title = "Compass",
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
            // Big Animated Compass Dial
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val onSurfaceColor = MaterialTheme.colorScheme.onSurfaceVariant
                val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
                val amberColor = ToolixAmber

                // Rotating dial ring
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedRotation)
                ) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Outer dial circle
                    drawCircle(
                        color = surfaceColor,
                        radius = radius,
                        style = Stroke(width = 4f)
                    )

                    // Tick marks
                    for (i in 0 until 360 step 15) {
                        val isMajor = (i % 90 == 0)
                        val isSemiMajor = (i % 45 == 0)
                        val tickLen = if (isMajor) 24f else if (isSemiMajor) 16f else 8f
                        val tickWidth = if (isMajor) 4f else 2f
                        val tickColor = if (i == 0) Color(0xFFFF5252) else onSurfaceColor

                        rotate(degrees = i.toFloat(), pivot = center) {
                            drawLine(
                                color = tickColor,
                                start = Offset(center.x, center.y - radius),
                                end = Offset(center.x, center.y - radius + tickLen),
                                strokeWidth = tickWidth
                            )
                        }
                    }
                }

                // Fixed Pointer Needle (North is red, South is light)
                Canvas(modifier = Modifier.size(140.dp)) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f

                    // North Arrow (Pointing UP)
                    val northPath = Path().apply {
                        moveTo(cx, 0f)
                        lineTo(cx - 14f, cy)
                        lineTo(cx + 14f, cy)
                        close()
                    }
                    drawPath(northPath, color = Color(0xFFFF5252))

                    // South Arrow (Pointing DOWN)
                    val southPath = Path().apply {
                        moveTo(cx, h)
                        lineTo(cx - 14f, cy)
                        lineTo(cx + 14f, cy)
                        close()
                    }
                    drawPath(southPath, color = onSurfaceColor)

                    // Center pin
                    drawCircle(color = Color.White, radius = 6f, center = Offset(cx, cy))
                }
            }

            ResultCard(
                label = "Compass Heading",
                value = "${azimuthDegrees.toInt()}°",
                subtitle = cardinal,
                accentColor = MaterialTheme.colorScheme.primary
            )

            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Calibration Tip", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "For optimal accuracy, move your device in a figure-8 motion away from magnetic interference or metallic objects.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
