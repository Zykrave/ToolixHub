package com.zykrave.toolixhub.ui.screens.tools.sensors

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.ToolixAmber

@Composable
fun FlashlightScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Camera Torch, 1: Screen Light
    var isTorchOn by remember { mutableStateOf(false) }

    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager }
    val cameraId = remember {
        try {
            cameraManager?.cameraIdList?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun setTorch(on: Boolean) {
        try {
            if (cameraId != null) {
                cameraManager?.setTorchMode(cameraId, on)
                isTorchOn = on
            }
        } catch (e: Exception) {
            // Torch not supported on some emulators or devices
            isTorchOn = on
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isTorchOn && cameraId != null) {
                try {
                    cameraManager?.setTorchMode(cameraId, false)
                } catch (e: Exception) {}
            }
        }
    }

    // Screen light properties
    var screenLightColor by remember { mutableStateOf(Color.White) }
    var screenBrightness by remember { mutableFloatStateOf(1.0f) }

    val lightColorOptions = listOf(
        "White" to Color.White,
        "Amber" to Color(0xFFFFB300),
        "Red Night" to Color(0xFFFF2A2A),
        "Soft Blue" to Color(0xFF00E5FF),
        "Emerald" to Color(0xFF00E676)
    )

    ToolixToolScaffold(
        title = "Flashlight & Screen Lamp",
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Rear Torch") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Screen Light") })
            }

            if (selectedTab == 0) {
                // Torch UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(if (isTorchOn) ToolixAmber.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(3.dp, if (isTorchOn) ToolixAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), CircleShape)
                            .clickable { setTorch(!isTorchOn) }
                            .testTag("torch_toggle_circle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Torch Switch",
                            modifier = Modifier.size(64.dp),
                            tint = if (isTorchOn) ToolixAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = if (isTorchOn) "TORCH ON" else "TORCH OFF",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isTorchOn) ToolixAmber else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { setTorch(!isTorchOn) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTorchOn) ToolixAmber else MaterialTheme.colorScheme.primary,
                            contentColor = if (isTorchOn) Color.Black else MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("toggle_torch_button")
                    ) {
                        Text(if (isTorchOn) "Turn Off Flashlight" else "Turn On Flashlight", fontSize = 16.sp)
                    }
                }
            } else {
                // Screen Light UI
                val effectiveColor = screenLightColor.copy(alpha = screenBrightness)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(effectiveColor)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f))

                    ToolixCard(
                        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Screen Light Controls", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                            Text("Color Preset", style = MaterialTheme.typography.labelMedium)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                lightColorOptions.forEach { (name, color) ->
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .border(
                                                width = if (screenLightColor == color) 3.dp else 1.dp,
                                                color = if (screenLightColor == color) MaterialTheme.colorScheme.primary else Color.Gray,
                                                shape = CircleShape
                                            )
                                            .clickable { screenLightColor = color }
                                    )
                                }
                            }

                            Text("Brightness: ${(screenBrightness * 100).toInt()}%", fontWeight = FontWeight.Medium)
                            Slider(
                                value = screenBrightness,
                                onValueChange = { screenBrightness = it },
                                valueRange = 0.2f..1.0f,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
