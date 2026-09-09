package com.zykrave.toolixhub.ui.screens.tools.color

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold

@Composable
fun ColorPaletteScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var baseHue by remember { mutableFloatStateOf(180f) }
    var harmonyMode by remember { mutableIntStateOf(0) } // 0: Complementary, 1: Analogous, 2: Triadic, 3: Monochromatic

    val modes = listOf("Complementary", "Analogous", "Triadic", "Monochromatic")
    val context = LocalContext.current

    fun hslToColor(h: Float, s: Float, l: Float): Color {
        val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
        val x = c * (1f - kotlin.math.abs((h / 60f) % 2f - 1f))
        val m = l - c / 2f

        val (rPrime, gPrime, bPrime) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        return Color(rPrime + m, gPrime + m, bPrime + m)
    }

    fun colorToHex(c: Color): String {
        val r = (c.red * 255).toInt()
        val g = (c.green * 255).toInt()
        val b = (c.blue * 255).toInt()
        return String.format("#%02X%02X%02X", r, g, b)
    }

    val palette = remember(baseHue, harmonyMode) {
        when (harmonyMode) {
            0 -> listOf(
                hslToColor(baseHue, 0.7f, 0.35f),
                hslToColor(baseHue, 0.8f, 0.55f),
                hslToColor((baseHue + 30f) % 360f, 0.6f, 0.6f),
                hslToColor((baseHue + 180f) % 360f, 0.8f, 0.55f),
                hslToColor((baseHue + 180f) % 360f, 0.7f, 0.35f)
            )
            1 -> listOf(
                hslToColor((baseHue - 40f + 360f) % 360f, 0.75f, 0.5f),
                hslToColor((baseHue - 20f + 360f) % 360f, 0.75f, 0.55f),
                hslToColor(baseHue, 0.8f, 0.5f),
                hslToColor((baseHue + 20f) % 360f, 0.75f, 0.55f),
                hslToColor((baseHue + 40f) % 360f, 0.75f, 0.5f)
            )
            2 -> listOf(
                hslToColor(baseHue, 0.8f, 0.45f),
                hslToColor(baseHue, 0.7f, 0.65f),
                hslToColor((baseHue + 120f) % 360f, 0.8f, 0.5f),
                hslToColor((baseHue + 240f) % 360f, 0.8f, 0.5f),
                hslToColor((baseHue + 240f) % 360f, 0.7f, 0.7f)
            )
            else -> listOf(
                hslToColor(baseHue, 0.7f, 0.2f),
                hslToColor(baseHue, 0.75f, 0.35f),
                hslToColor(baseHue, 0.8f, 0.5f),
                hslToColor(baseHue, 0.65f, 0.65f),
                hslToColor(baseHue, 0.5f, 0.8f)
            )
        }
    }

    ToolixToolScaffold(
        title = "Palette Generator",
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
                    Text("Harmony Rules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        modes.take(2).forEachIndexed { index, label ->
                            FilterChip(
                                selected = harmonyMode == index,
                                onClick = { harmonyMode = index },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        modes.drop(2).forEachIndexed { index, label ->
                            val actual = index + 2
                            FilterChip(
                                selected = harmonyMode == actual,
                                onClick = { harmonyMode = actual },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Text("Base Hue: ${baseHue.toInt()}°", fontWeight = FontWeight.Medium)
                    Slider(
                        value = baseHue,
                        onValueChange = { baseHue = it },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Text("Palette Result (Tap to copy HEX)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            // Combined color bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                palette.forEach { c ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .background(c)
                    )
                }
            }

            // Individual cards
            palette.forEachIndexed { index, c ->
                val hex = colorToHex(c)
                ToolixCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(c)
                            )
                            Column {
                                Text(hex, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Color #${index + 1}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("HEX", hex))
                                Toast.makeText(context, "Copied $hex", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy hex")
                        }
                    }
                }
            }
        }
    }
}
