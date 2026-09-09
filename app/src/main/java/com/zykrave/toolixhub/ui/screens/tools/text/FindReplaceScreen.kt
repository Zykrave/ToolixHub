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
fun FindReplaceScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var textInput by remember {
        mutableStateOf("ToolixHub multitool is fast. ToolixHub multitool is offline. Everything in ToolixHub is built for efficiency.")
    }
    var findQuery by remember { mutableStateOf("ToolixHub") }
    var replaceQuery by remember { mutableStateOf("Swiss") }
    var matchCase by remember { mutableStateOf(true) }

    val occurrences = if (findQuery.isEmpty()) 0 else {
        var count = 0
        var startIndex = 0
        while (startIndex < textInput.length) {
            val found = textInput.indexOf(findQuery, startIndex, ignoreCase = !matchCase)
            if (found >= 0) {
                count++
                startIndex = found + findQuery.length
            } else {
                break
            }
        }
        count
    }

    val replacedText = if (findQuery.isEmpty()) textInput else {
        textInput.replace(findQuery, replaceQuery, ignoreCase = !matchCase)
    }

    val context = LocalContext.current

    ToolixToolScaffold(
        title = "Find & Replace",
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
                    Text("Original Text", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )

                    OutlinedTextField(
                        value = findQuery,
                        onValueChange = { findQuery = it },
                        label = { Text("Find Text") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = replaceQuery,
                        onValueChange = { replaceQuery = it },
                        label = { Text("Replace With") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = matchCase, onCheckedChange = { matchCase = it })
                        Text("Match Case")
                    }

                    Button(
                        onClick = {
                            textInput = replacedText
                            Toast.makeText(context, "Applied replacements to original!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply to Original Text")
                    }
                }
            }

            ResultCard(
                label = "Preview Result ($occurrences occurrences found)",
                value = replacedText,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Replaced Text", replacedText))
                    Toast.makeText(context, "Copied replaced text!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
