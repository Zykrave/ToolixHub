package com.zykrave.toolixhub.ui.screens.tools.sensors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.text.DecimalFormat
import kotlin.math.abs

@Composable
fun RulerScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val yDpi = context.resources.displayMetrics.ydpi.coerceAtLeast(160f)
    val pixelsPerMm = yDpi / 25.4f
    val pixelsPerInch = yDpi

    var unitMode by remember { mutableIntStateOf(0) } // 0: Metric (cm/mm), 1: Imperial (inches)

    var caliperTopY by remember { mutableFloatStateOf(100f) }
    var caliperBottomY by remember { mutableFloatStateOf(500f) }

    val distancePixels = abs(caliperBottomY - caliperTopY)
    val distanceMm = distancePixels / pixelsPerMm
    val distanceCm = distanceMm / 10f
    val distanceInches = distancePixels / pixelsPerInch

    val dfCm = DecimalFormat("#0.0 cm")
    val dfMm = DecimalFormat("#0 mm")
    val dfIn = DecimalFormat("#0.00 in")

    ToolixToolScaffold(
        title = "On-Screen Caliper Ruler",
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Control and Readout Header
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (unitMode == 0) "${dfCm.format(distanceCm)} (${dfMm.format(distanceMm)})" else dfIn.format(distanceInches),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text("Drag calipers to measure", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = unitMode == 0,
                            onClick = { unitMode = 0 },
                            label = { Text("Metric (cm)") }
                        )
                        FilterChip(
                            selected = unitMode == 1,
                            onClick = { unitMode = 1 },
                            label = { Text("Imperial (in)") }
                        )
                    }
                }
            }

            // Interactive Ruler Canvas with Draggable Calipers
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                val lineColor = MaterialTheme.colorScheme.onSurfaceVariant
                val primary = MaterialTheme.colorScheme.primary

                // Drawn Ruler Scale
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    if (unitMode == 0) {
                        // Metric scale (mm / cm)
                        var curY = 0f
                        var mmCount = 0
                        while (curY < h) {
                            val isCm = (mmCount % 10 == 0)
                            val isHalfCm = (mmCount % 5 == 0)
                            val tickLen = if (isCm) 60f else if (isHalfCm) 40f else 22f
                            val tickStroke = if (isCm) 3f else 1.5f

                            drawLine(
                                color = lineColor,
                                start = Offset(0f, curY),
                                end = Offset(tickLen, curY),
                                strokeWidth = tickStroke
                            )

                            curY += pixelsPerMm
                            mmCount++
                        }
                    } else {
                        // Imperial scale (inches / 16ths)
                        val step = pixelsPerInch / 16f
                        var curY = 0f
                        var stepCount = 0
                        while (curY < h) {
                            val isInch = (stepCount % 16 == 0)
                            val isHalf = (stepCount % 8 == 0)
                            val isQuarter = (stepCount % 4 == 0)
                            val tickLen = when {
                                isInch -> 70f
                                isHalf -> 50f
                                isQuarter -> 36f
                                else -> 20f
                            }

                            drawLine(
                                color = lineColor,
                                start = Offset(0f, curY),
                                end = Offset(tickLen, curY),
                                strokeWidth = if (isInch) 3f else 1.5f
                            )

                            curY += step
                            stepCount++
                        }
                    }
                }

                // Caliper 1 (Top)
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, caliperTopY.toInt()) }
                        .fillMaxWidth()
                        .height(44.dp)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()
                                caliperTopY = (caliperTopY + dragAmount).coerceIn(0f, 2000f)
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(primary)
                            .align(Alignment.Center)
                    )
                    Surface(
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        color = primary,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text(
                            text = " ▲ TOP ",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Caliper 2 (Bottom)
                Box(
                    modifier = Modifier
                        .offset { IntOffset(0, caliperBottomY.toInt()) }
                        .fillMaxWidth()
                        .height(44.dp)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { change, dragAmount ->
                                change.consume()
                                caliperBottomY = (caliperBottomY + dragAmount).coerceIn(0f, 2000f)
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(primary)
                            .align(Alignment.Center)
                    )
                    Surface(
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        color = primary,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text(
                            text = " ▼ BOTTOM ",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
