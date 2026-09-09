package com.zykrave.toolixhub.ui.screens.tools.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold

@Composable
fun TextCounterScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }

    val characterCount = textInput.length
    val characterNoSpaces = textInput.count { !it.isWhitespace() }
    val words = textInput.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    val wordCount = if (textInput.isBlank()) 0 else words.size
    val sentences = textInput.split("[.!?]+".toRegex()).filter { it.trim().isNotBlank() }
    val sentenceCount = if (textInput.isBlank()) 0 else sentences.size
    val paragraphs = textInput.split("\n\n+".toRegex()).filter { it.trim().isNotBlank() }
    val paragraphCount = if (textInput.isBlank()) 0 else paragraphs.size

    // Average reading speed: 200 words/min; Speech speed: 130 words/min
    val readingMinutes = (wordCount / 200.0)
    val readingSeconds = (readingMinutes * 60).toInt()
    val readingTimeStr = if (readingSeconds < 60) "${readingSeconds}s" else "${readingSeconds / 60}m ${readingSeconds % 60}s"

    ToolixToolScaffold(
        title = "Word & Char Counter",
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        actions = {
            if (textInput.isNotEmpty()) {
                TextButton(onClick = { textInput = "" }) {
                    Text("Clear", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Enter or paste text", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Type or paste your text here to analyze...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("text_counter_input")
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultCard(
                    label = "Words",
                    value = "$wordCount",
                    modifier = Modifier.weight(1f)
                )
                ResultCard(
                    label = "Characters",
                    value = "$characterCount",
                    subtitle = "$characterNoSpaces without spaces",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ResultCard(
                    label = "Sentences",
                    value = "$sentenceCount",
                    modifier = Modifier.weight(1f)
                )
                ResultCard(
                    label = "Paragraphs",
                    value = "$paragraphCount",
                    modifier = Modifier.weight(1f)
                )
            }

            ResultCard(
                label = "Estimated Reading Time",
                value = readingTimeStr,
                subtitle = "Based on standard reading speed of 200 wpm"
            )
        }
    }
}
