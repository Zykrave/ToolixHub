package com.zykrave.toolixhub.ui.screens.tools.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold

@Composable
fun LoremIpsumScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var count by remember { mutableIntStateOf(3) }
    var type by remember { mutableIntStateOf(0) } // 0: Paragraphs, 1: Sentences, 2: Words
    var startWithStandard by remember { mutableStateOf(true) }

    val latinWords = listOf(
        "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
        "sed", "do", "eiusmod", "tempor", "incididunt", "ut", "labore", "et", "dolore",
        "magna", "aliqua", "enim", "ad", "minim", "veniam", "quis", "nostrud",
        "exercitation", "ullamco", "laboris", "nisi", "aliquip", "ex", "ea", "commodo",
        "consequat", "duis", "aute", "irure", "in", "reprehenderit", "voluptate",
        "velit", "esse", "cillum", "fugiat", "nulla", "pariatur", "excepteur", "sint",
        "occaecat", "cupidatat", "non", "proident", "sunt", "culpa", "qui", "officia",
        "deserunt", "mollit", "anim", "id", "est", "laborum"
    )

    fun generateSentence(): String {
        val len = (8..16).random()
        val words = (1..len).map { latinWords.random() }.toMutableList()
        words[0] = words[0].replaceFirstChar { it.uppercaseChar() }
        return words.joinToString(" ") + "."
    }

    fun generateParagraph(): String {
        val sentences = (4..7).map { generateSentence() }
        return sentences.joinToString(" ")
    }

    fun generateText(): String {
        return when (type) {
            0 -> {
                val paras = (1..count).map { generateParagraph() }.toMutableList()
                if (startWithStandard && paras.isNotEmpty()) {
                    paras[0] = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                            paras[0].substringAfter(". ", "")
                }
                paras.joinToString("\n\n")
            }
            1 -> {
                val sentences = (1..count).map { generateSentence() }.toMutableList()
                if (startWithStandard && sentences.isNotEmpty()) {
                    sentences[0] = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
                }
                sentences.joinToString(" ")
            }
            else -> {
                val words = (1..count).map { latinWords.random() }.toMutableList()
                if (startWithStandard) {
                    val standardWords = listOf("lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit")
                    for (i in 0 until minOf(count, standardWords.size)) {
                        words[i] = standardWords[i]
                    }
                }
                words[0] = words[0].replaceFirstChar { it.uppercaseChar() }
                words.joinToString(" ")
            }
        }
    }

    var generatedText by remember(count, type, startWithStandard) { mutableStateOf(generateText()) }
    val context = LocalContext.current

    ToolixToolScaffold(
        title = "Lorem Ipsum Generator",
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
                    Text("Generator Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Text("Generate By", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Paragraphs", "Sentences", "Words").forEachIndexed { index, label ->
                            FilterChip(
                                selected = type == index,
                                onClick = {
                                    type = index
                                    count = if (index == 2) 30 else 3
                                },
                                label = { Text(label) }
                            )
                        }
                    }

                    Text("Quantity: $count", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    val maxRange = if (type == 2) 100f else 15f
                    Slider(
                        value = count.toFloat(),
                        onValueChange = { count = it.toInt() },
                        valueRange = 1f..maxRange,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = startWithStandard, onCheckedChange = { startWithStandard = it })
                        Text("Start with 'Lorem ipsum dolor sit amet...'")
                    }

                    Button(
                        onClick = { generatedText = generateText() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Regenerate")
                    }
                }
            }

            ResultCard(
                label = "Generated Text",
                value = generatedText,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Lorem Ipsum", generatedText))
                    Toast.makeText(context, "Copied lorem ipsum text!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
