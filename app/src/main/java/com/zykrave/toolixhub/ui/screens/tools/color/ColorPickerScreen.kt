package com.zykrave.toolixhub.ui.screens.tools.color

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold

@Composable
fun ColorPickerScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var red by remember { mutableFloatStateOf(0.12f) }
    var green by remember { mutableFloatStateOf(0.83f) }
    var blue by remember { mutableFloatStateOf(0.78f) }

    val currentColor = Color(red, green, blue)

    val r255 = (red * 255).toInt()
    val g255 = (green * 255).toInt()
    val b255 = (blue * 255).toInt()

    val hexCode = String.format("#%02X%02X%02X", r255, g255, b255)
    val rgbCode = "rgb($r255, $g255, $b255)"

    // Convert to HSL
    val rPrime = red
    val gPrime = green
    val bPrime = blue
    val cMax = maxOf(rPrime, gPrime, bPrime)
    val cMin = minOf(rPrime, gPrime, bPrime)
    val delta = cMax - cMin

    val lightness = (cMax + cMin) / 2f
    val saturation = if (delta == 0f) 0f else delta / (1f - kotlin.math.abs(2f * lightness - 1f))
    val hue = when {
        delta == 0f -> 0f
        cMax == rPrime -> 60f * (((gPrime - bPrime) / delta) % 6)
        cMax == gPrime -> 60f * (((bPrime - rPrime) / delta) + 2)
        else -> 60f * (((rPrime - gPrime) / delta) + 4)
    }.let { if (it < 0) it + 360f else it }

    val hslCode = "hsl(${hue.toInt()}°, ${(saturation * 100).toInt()}%, ${(lightness * 100).toInt()}%)"

    ToolixToolScaffold(
        title = "Color Picker & Converter",
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
            // Big Color Swatch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(currentColor)
                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = hexCode,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("RGB Channels", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Text("Red: $r255", color = Color(0xFFFF5252), fontWeight = FontWeight.Medium)
                    Slider(
                        value = red,
                        onValueChange = { red = it },
                        colors = SliderDefaults.colors(thumbColor = Color(0xFFFF5252), activeTrackColor = Color(0xFFFF5252)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Green: $g255", color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                    Slider(
                        value = green,
                        onValueChange = { green = it },
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF4CAF50), activeTrackColor = Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Blue: $b255", color = Color(0xFF448AFF), fontWeight = FontWeight.Medium)
                    Slider(
                        value = blue,
                        onValueChange = { blue = it },
                        colors = SliderDefaults.colors(thumbColor = Color(0xFF448AFF), activeTrackColor = Color(0xFF448AFF)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Text("Color Formats (Tap copy to use)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            ResultCard(
                label = "HEX Code",
                value = hexCode,
                accentColor = currentColor
            )

            ResultCard(
                label = "RGB Format",
                value = rgbCode,
                accentColor = currentColor
            )

            ResultCard(
                label = "HSL Format",
                value = hslCode,
                accentColor = currentColor
            )
        }
    }
}
