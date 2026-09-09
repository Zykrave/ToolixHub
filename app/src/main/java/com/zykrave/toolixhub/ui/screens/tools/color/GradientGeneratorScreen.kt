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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GradientGeneratorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var hue1 by remember { mutableFloatStateOf(175f) }
    var hue2 by remember { mutableFloatStateOf(265f) }
    var angleDeg by remember { mutableFloatStateOf(135f) }

    fun hslToColor(h: Float, s: Float, l: Float): Color {
        val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
        val x = c * (1f - kotlin.math.abs((h / 60f) % 2f - 1f))
        val m = l - c / 2f
        val (rP, gP, bP) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        return Color(rP + m, gP + m, bP + m)
    }

    val color1 = hslToColor(hue1, 0.85f, 0.55f)
    val color2 = hslToColor(hue2, 0.85f, 0.55f)

    fun colorToHex(c: Color): String {
        return String.format("#%02X%02X%02X", (c.red * 255).toInt(), (c.green * 255).toInt(), (c.blue * 255).toInt())
    }

    val hex1 = colorToHex(color1)
    val hex2 = colorToHex(color2)

    val rad = Math.toRadians(angleDeg.toDouble())
    val startOffset = Offset(
        (0.5f - 0.5f * cos(rad)).toFloat() * 1000f,
        (0.5f - 0.5f * sin(rad)).toFloat() * 1000f
    )
    val endOffset = Offset(
        (0.5f + 0.5f * cos(rad)).toFloat() * 1000f,
        (0.5f + 0.5f * sin(rad)).toFloat() * 1000f
    )

    val cssCode = "background: linear-gradient(${angleDeg.toInt()}deg, $hex1, $hex2);"
    val composeCode = "Brush.linearGradient(listOf(Color(0xFF${hex1.removePrefix("#")}), Color(0xFF${hex2.removePrefix("#")})))"

    val context = LocalContext.current

    ToolixToolScaffold(
        title = "Gradient Generator",
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
            // Live Gradient Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(color1, color2), start = startOffset, end = endOffset))
                    .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${angleDeg.toInt()}°",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Gradient Controls", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Text("Color 1 Hue: ${hue1.toInt()}° ($hex1)", fontWeight = FontWeight.Medium)
                    Slider(
                        value = hue1,
                        onValueChange = { hue1 = it },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Color 2 Hue: ${hue2.toInt()}° ($hex2)", fontWeight = FontWeight.Medium)
                    Slider(
                        value = hue2,
                        onValueChange = { hue2 = it },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Angle: ${angleDeg.toInt()}°", fontWeight = FontWeight.Medium)
                    Slider(
                        value = angleDeg,
                        onValueChange = { angleDeg = it },
                        valueRange = 0f..360f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            ResultCard(
                label = "CSS Code",
                value = cssCode,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("CSS Gradient", cssCode))
                    Toast.makeText(context, "Copied CSS code!", Toast.LENGTH_SHORT).show()
                }
            )

            ResultCard(
                label = "Jetpack Compose Snippet",
                value = composeCode,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Compose Gradient", composeCode))
                    Toast.makeText(context, "Copied Jetpack Compose code!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
