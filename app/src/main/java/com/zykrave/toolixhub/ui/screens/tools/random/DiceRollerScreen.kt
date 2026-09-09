package com.zykrave.toolixhub.ui.screens.tools.random

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiceRollerScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }
    val scope = rememberCoroutineScope()

    val diceTypes = listOf(4, 6, 8, 10, 12, 20, 100)
    var selectedDie by remember { mutableIntStateOf(6) } // d6
    var diceCount by remember { mutableIntStateOf(2) }
    var modifier by remember { mutableIntStateOf(0) }

    val currentRolls = remember { mutableStateListOf(3, 5) }
    var totalSum by remember { mutableIntStateOf(8) }

    val rotationAnim = remember { Animatable(0f) }

    fun triggerVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(50)
        }
    }

    fun rollDice() {
        triggerVibration()
        scope.launch {
            rotationAnim.snapTo(0f)
            rotationAnim.animateTo(360f, animationSpec = tween(350))
        }
        currentRolls.clear()
        var sum = 0
        for (i in 0 until diceCount) {
            val r = Random.nextInt(1, selectedDie + 1)
            currentRolls.add(r)
            sum += r
        }
        totalSum = sum + modifier
    }

    ToolixToolScaffold(
        title = "Dice Roller",
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
                    Text("Select Die Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        diceTypes.forEach { d ->
                            FilterChip(
                                selected = selectedDie == d,
                                onClick = {
                                    selectedDie = d
                                    rollDice()
                                },
                                label = { Text("d$d") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Text("Number of Dice: $diceCount", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Slider(
                        value = diceCount.toFloat(),
                        onValueChange = {
                            diceCount = it.toInt()
                            rollDice()
                        },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Modifier: ${if (modifier >= 0) "+$modifier" else "$modifier"}", fontWeight = FontWeight.Medium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(-5, -1, 0, 1, 5).forEach { mod ->
                                FilterChip(
                                    selected = modifier == mod,
                                    onClick = {
                                        modifier = mod
                                        totalSum = currentRolls.sum() + mod
                                    },
                                    label = { Text(if (mod > 0) "+$mod" else "$mod") }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { rollDice() },
                        modifier = Modifier.fillMaxWidth().testTag("roll_dice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotationAnim.value)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Roll Dice ($diceCount d$selectedDie)")
                    }
                }
            }

            // Dice Display
            ToolixCard {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "ROLL RESULT",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "$totalSum",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentRolls.forEachIndexed { idx, value ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$value",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    }

                    val diceBreakdown = currentRolls.joinToString(" + ")
                    val modStr = if (modifier > 0) " + $modifier" else if (modifier < 0) " - ${kotlin.math.abs(modifier)}" else ""
                    Text(
                        text = "($diceBreakdown)$modStr = $totalSum",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
