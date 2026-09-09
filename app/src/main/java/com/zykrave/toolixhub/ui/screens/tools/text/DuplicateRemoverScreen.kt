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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun DuplicateRemoverScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var textInput by remember {
        mutableStateOf("apple\nbanana\napple\norange\nbanana\ngrape\nAPPLE")
    }
    var ignoreCase by remember { mutableStateOf(false) }
    var trimLines by remember { mutableStateOf(true) }

    val rawLines = textInput.split("\n")
    val seen = mutableSetOf<String>()
    val resultLines = mutableListOf<String>()

    for (raw in rawLines) {
        val line = if (trimLines) raw.trim() else raw
        val key = if (ignoreCase) line.lowercase() else line
        if (!seen.contains(key)) {
            seen.add(key)
            resultLines.add(line)
        }
    }

    val removedCount = rawLines.size - resultLines.size
    val context = LocalContext.current
    val outputText = resultLines.joinToString("\n")

    ToolixToolScaffold(
        title = "Duplicate Line Remover",
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
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = ignoreCase, onCheckedChange = { ignoreCase = it })
                        Text("Ignore case differences (e.g. Apple == apple)")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = trimLines, onCheckedChange = { trimLines = it })
                        Text("Trim leading & trailing whitespace")
                    }
                }
            }

            ResultCard(
                label = "Filtered Result ($removedCount duplicates removed)",
                value = outputText,
                subtitle = "Original: ${rawLines.size} lines | Cleaned: ${resultLines.size} lines",
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Cleaned Lines", outputText))
                    Toast.makeText(context, "Copied cleaned lines!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
