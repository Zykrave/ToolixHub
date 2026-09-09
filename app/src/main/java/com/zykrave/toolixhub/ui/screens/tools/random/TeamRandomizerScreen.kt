package com.zykrave.toolixhub.ui.screens.tools.random

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold

@Composable
fun TeamRandomizerScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var namesInput by remember {
        mutableStateOf("Liam\nNoah\nOliver\nEmma\nCharlotte\nAmelia\nLucas\nMia\nHarper\nEvelyn")
    }
    var teamCount by remember { mutableIntStateOf(2) }

    val teams = remember { mutableStateListOf<List<String>>() }

    val teamNames = listOf("Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel")

    fun generateTeams() {
        val names = namesInput.split("\n").map { it.trim() }.filter { it.isNotBlank() }.shuffled()
        teams.clear()
        if (names.isEmpty()) return

        val result = List(teamCount) { mutableListOf<String>() }
        names.forEachIndexed { index, name ->
            result[index % teamCount].add(name)
        }
        teams.addAll(result)
    }

    ToolixToolScaffold(
        title = "Team Randomizer",
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
                    Text("Participants (One per line)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = namesInput,
                        onValueChange = { namesInput = it },
                        modifier = Modifier.fillMaxWidth().height(150.dp).testTag("team_names_input")
                    )

                    Text("Number of Teams: $teamCount", fontWeight = FontWeight.Medium)
                    Slider(
                        value = teamCount.toFloat(),
                        onValueChange = { teamCount = it.toInt() },
                        valueRange = 2f..6f,
                        steps = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { generateTeams() },
                        modifier = Modifier.fillMaxWidth().testTag("generate_teams_button")
                    ) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null)
                        Text(" Randomize into $teamCount Teams")
                    }
                }
            }

            if (teams.isNotEmpty()) {
                teams.forEachIndexed { index, members ->
                    val name = teamNames.getOrElse(index) { "Team ${index + 1}" }
                    ToolixCard {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Team $name", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text("${members.size} members", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            members.forEach { m ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = m,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
}
