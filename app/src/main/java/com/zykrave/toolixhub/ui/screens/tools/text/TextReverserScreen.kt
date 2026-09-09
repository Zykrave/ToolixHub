package com.zykrave.toolixhub.ui.screens.tools.text

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold

@Composable
fun TextReverserScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var textInput by remember { mutableStateOf("Never odd or even.\nA man, a plan, a canal: Panama!") }
    var mode by remember { mutableIntStateOf(0) } // 0: Characters, 1: Words, 2: Lines

    val reversedText = when (mode) {
        0 -> textInput.reversed()
        1 -> textInput.split("\n").joinToString("\n") { line ->
            line.split("\\s+".toRegex()).reversed().joinToString(" ")
        }
        else -> textInput.split("\n").reversed().joinToString("\n")
    }

    val context = LocalContext.current

    ToolixToolScaffold(
        title = "Text Reverser",
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
                    Text("Input Text", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )

                    Text("Reverse Mode", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = mode == 0,
                            onClick = { mode = 0 },
                            label = { Text("All Characters") }
                        )
                        FilterChip(
                            selected = mode == 1,
                            onClick = { mode = 1 },
                            label = { Text("Word Order") }
                        )
                        FilterChip(
                            selected = mode == 2,
                            onClick = { mode = 2 },
                            label = { Text("Line Order") }
                        )
                    }
                }
            }

            ResultCard(
                label = "Reversed Output",
                value = reversedText,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Reversed Text", reversedText))
                    Toast.makeText(context, "Copied reversed text!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
