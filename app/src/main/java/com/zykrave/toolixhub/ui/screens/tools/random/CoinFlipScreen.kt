package com.zykrave.toolixhub.ui.screens.tools.random

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.ToolixAmber
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun CoinFlipScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }
    val scope = rememberCoroutineScope()

    var lastResult by remember { mutableStateOf("HEADS") }
    var headsCount by remember { mutableIntStateOf(0) }
    var tailsCount by remember { mutableIntStateOf(0) }
    val flipHistory = remember { mutableStateListOf<String>() }

    val rotation = remember { Animatable(0f) }

    fun triggerVibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(40)
        }
    }

    fun flip(times: Int = 1) {
        triggerVibrate()
        scope.launch {
            rotation.snapTo(0f)
            rotation.animateTo(720f, animationSpec = tween(500))
        }

        repeat(times) {
            val isHeads = Random.nextBoolean()
            val outcome = if (isHeads) "HEADS" else "TAILS"
            lastResult = outcome
            if (isHeads) headsCount++ else tailsCount++
            flipHistory.add(0, outcome)
        }
    }

    val totalFlips = headsCount + tailsCount
    val headsPct = if (totalFlips > 0) (headsCount.toDouble() / totalFlips * 100).toInt() else 50
    val tailsPct = if (totalFlips > 0) (tailsCount.toDouble() / totalFlips * 100).toInt() else 50

    ToolixToolScaffold(
        title = "Coin Flipper",
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
            // Interactive 3D Coin
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        rotationY = rotation.value
                        cameraDistance = 12f * density
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (lastResult == "HEADS") ToolixAmber else MaterialTheme.colorScheme.primary,
                    border = BorderStroke(4.dp, MaterialTheme.colorScheme.background),
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(150.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (lastResult == "HEADS") "H" else "T",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                }
            }

            Text(
                text = lastResult,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { flip(1) },
                    modifier = Modifier.weight(1f).height(48.dp).testTag("flip_coin_button")
                ) {
                    Text("Flip 1×")
                }
                OutlinedButton(
                    onClick = { flip(3) },
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Flip 3×")
                }
                OutlinedButton(
                    onClick = { flip(5) },
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Flip 5×")
                }
            }

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultCard(
                    label = "Heads",
                    value = "$headsCount",
                    subtitle = "$headsPct% of total",
                    modifier = Modifier.weight(1f),
                    accentColor = ToolixAmber
                )
                ResultCard(
                    label = "Tails",
                    value = "$tailsCount",
                    subtitle = "$tailsPct% of total",
                    modifier = Modifier.weight(1f),
                    accentColor = MaterialTheme.colorScheme.primary
                )
            }

            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Flips (Last 10)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        if (totalFlips > 0) {
                            OutlinedButton(
                                onClick = {
                                    headsCount = 0
                                    tailsCount = 0
                                    flipHistory.clear()
                                }
                            ) {
                                Text("Reset")
                            }
                        }
                    }

                    if (flipHistory.isEmpty()) {
                        Text("No flips yet. Tap Flip to start.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Text(
                            text = flipHistory.take(10).joinToString(" → "),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
