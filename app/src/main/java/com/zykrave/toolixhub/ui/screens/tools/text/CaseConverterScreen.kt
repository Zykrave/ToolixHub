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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.util.Locale

@Composable
fun CaseConverterScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var textInput by remember { mutableStateOf("The quick brown fox jumps over the lazy dog.") }
    val context = LocalContext.current

    fun toTitleCase(s: String): String {
        return s.split(" ").joinToString(" ") { word ->
            word.lowercase(Locale.ROOT).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        }
    }

    fun toSentenceCase(s: String): String {
        var capitalizeNext = true
        val result = StringBuilder()
        for (char in s.lowercase(Locale.ROOT)) {
            if (capitalizeNext && char.isLetter()) {
                result.append(char.uppercaseChar())
                capitalizeNext = false
            } else {
                result.append(char)
            }
            if (char in listOf('.', '!', '?')) {
                capitalizeNext = true
            }
        }
        return result.toString()
    }

    fun toCamelCase(s: String): String {
        val words = s.trim().split("[\\s_\\-]+".toRegex()).filter { it.isNotBlank() }
        if (words.isEmpty()) return ""
        return words[0].lowercase(Locale.ROOT) + words.drop(1).joinToString("") {
            it.lowercase(Locale.ROOT).replaceFirstChar { c -> c.uppercaseChar() }
        }
    }

    fun toSnakeCase(s: String): String {
        return s.trim().split("[\\s_\\-]+".toRegex()).filter { it.isNotBlank() }.joinToString("_") { it.lowercase(Locale.ROOT) }
    }

    fun toConstantCase(s: String): String {
        return s.trim().split("[\\s_\\-]+".toRegex()).filter { it.isNotBlank() }.joinToString("_") { it.uppercase(Locale.ROOT) }
    }

    fun toKebabCase(s: String): String {
        return s.trim().split("[\\s_\\-]+".toRegex()).filter { it.isNotBlank() }.joinToString("-") { it.lowercase(Locale.ROOT) }
    }

    ToolixToolScaffold(
        title = "Case Converter",
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("case_converter_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { textInput = "" },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Clear")
                        }
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("ToolixHub Case", textInput))
                                Toast.makeText(context, "Copied result!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Copy")
                        }
                    }
                }
            }

            Text("Transform Cases", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            val options = listOf(
                "UPPERCASE" to { textInput.uppercase(Locale.ROOT) },
                "lowercase" to { textInput.lowercase(Locale.ROOT) },
                "Title Case" to { toTitleCase(textInput) },
                "Sentence case" to { toSentenceCase(textInput) },
                "camelCase" to { toCamelCase(textInput) },
                "snake_case" to { toSnakeCase(textInput) },
                "CONSTANT_CASE" to { toConstantCase(textInput) },
                "kebab-case" to { toKebabCase(textInput) }
            )

            options.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { (label, transformer) ->
                        OutlinedButton(
                            onClick = { textInput = transformer() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) {
                            Text(label, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
