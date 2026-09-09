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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold

@Composable
fun LineSorterScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var textInput by remember {
        mutableStateOf("Zebra\nApple\nElephant\nBanana\nOrange\nCat\nDog")
    }

    val context = LocalContext.current

    fun sortAZ() {
        val lines = textInput.split("\n")
        textInput = lines.sortedWith(String.CASE_INSENSITIVE_ORDER).joinToString("\n")
    }

    fun sortZA() {
        val lines = textInput.split("\n")
        textInput = lines.sortedWith(String.CASE_INSENSITIVE_ORDER.reversed()).joinToString("\n")
    }

    fun sortByLengthAsc() {
        val lines = textInput.split("\n")
        textInput = lines.sortedBy { it.length }.joinToString("\n")
    }

    fun sortByLengthDesc() {
        val lines = textInput.split("\n")
        textInput = lines.sortedByDescending { it.length }.joinToString("\n")
    }

    fun shuffleLines() {
        val lines = textInput.split("\n")
        textInput = lines.shuffled().joinToString("\n")
    }

    fun removeBlank() {
        val lines = textInput.split("\n")
        textInput = lines.filter { it.isNotBlank() }.joinToString("\n")
    }

    ToolixToolScaffold(
        title = "Line Sorter",
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
                    Text("Lines to Sort", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    )
                }
            }

            Text("Sorting Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { sortAZ() }, modifier = Modifier.weight(1f)) {
                    Text("A → Z")
                }
                OutlinedButton(onClick = { sortZA() }, modifier = Modifier.weight(1f)) {
                    Text("Z → A")
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { sortByLengthAsc() }, modifier = Modifier.weight(1f)) {
                    Text("Shortest First")
                }
                OutlinedButton(onClick = { sortByLengthDesc() }, modifier = Modifier.weight(1f)) {
                    Text("Longest First")
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { shuffleLines() }, modifier = Modifier.weight(1f)) {
                    Text("Shuffle Random")
                }
                OutlinedButton(onClick = { removeBlank() }, modifier = Modifier.weight(1f)) {
                    Text("Remove Blanks")
                }
            }

            ResultCard(
                label = "Current Lines Output",
                value = textInput,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Sorted Lines", textInput))
                    Toast.makeText(context, "Copied sorted lines!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
