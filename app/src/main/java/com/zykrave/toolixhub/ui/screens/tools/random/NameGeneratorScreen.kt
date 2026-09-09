package com.zykrave.toolixhub.ui.screens.tools.random

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.zykrave.toolixhub.ui.theme.ToolixAmber

@Composable
fun NameGeneratorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var namesInput by remember {
        mutableStateOf("Alice\nBob\nCharlie\nDiana\nEthan\nFiona\nGeorge")
    }
    var winner by remember { mutableStateOf<String?>(null) }
    val shuffledList = remember { mutableStateListOf<String>() }

    fun pickWinner() {
        val names = namesInput.split("\n").map { it.trim() }.filter { it.isNotBlank() }
        if (names.isNotEmpty()) {
            winner = names.random()
        }
    }

    fun shuffleAll() {
        val names = namesInput.split("\n").map { it.trim() }.filter { it.isNotBlank() }
        shuffledList.clear()
        shuffledList.addAll(names.shuffled())
        winner = null
    }

    ToolixToolScaffold(
        title = "Random Name Picker",
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
                    Text("Enter Names (One per line)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = namesInput,
                        onValueChange = { namesInput = it },
                        modifier = Modifier.fillMaxWidth().height(160.dp).testTag("names_input_area")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { pickWinner() },
                            modifier = Modifier.weight(1f).testTag("pick_winner_button")
                        ) {
                            Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null)
                            Text(" Pick 1 Winner", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { shuffleAll() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Shuffle Order")
                        }
                    }
                }
            }

            AnimatedVisibility(visible = winner != null) {
                winner?.let { w ->
                    ToolixCard(
                        borderColor = ToolixAmber,
                        backgroundColor = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = ToolixAmber,
                                modifier = Modifier.height(48.dp)
                            )
                            Text("SELECTED WINNER", style = MaterialTheme.typography.labelSmall, color = ToolixAmber, fontWeight = FontWeight.Bold)
                            Text(w, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (shuffledList.isNotEmpty()) {
                ToolixCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Randomized Order", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        shuffledList.forEachIndexed { index, name ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${index + 1}. $name",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
