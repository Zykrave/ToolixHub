package com.zykrave.toolixhub.ui.screens.tools.utility

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Base64
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.security.MessageDigest

@Composable
fun HashGeneratorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var textInput by remember { mutableStateOf("ToolixHub Offline Multitool") }
    var uppercase by remember { mutableStateOf(false) }
    val context = LocalContext.current

    fun computeHash(algorithm: String, input: String): String {
        return try {
            val md = MessageDigest.getInstance(algorithm)
            val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
            val sb = StringBuilder()
            for (b in bytes) {
                sb.append(String.format("%02x", b))
            }
            val res = sb.toString()
            if (uppercase) res.uppercase() else res
        } catch (e: Exception) {
            "Error"
        }
    }

    val md5 = remember(textInput, uppercase) { computeHash("MD5", textInput) }
    val sha1 = remember(textInput, uppercase) { computeHash("SHA-1", textInput) }
    val sha256 = remember(textInput, uppercase) { computeHash("SHA-256", textInput) }
    val sha512 = remember(textInput, uppercase) { computeHash("SHA-512", textInput) }
    val base64 = remember(textInput) {
        Base64.encodeToString(textInput.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    ToolixToolScaffold(
        title = "Hash & Checksum Generator",
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
                    Text("Input String", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp).testTag("hash_input_field")
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = uppercase, onCheckedChange = { uppercase = it })
                        Text("Uppercase Hashes (A-F)")
                    }
                }
            }

            Text("Computed Hashes (Tap copy to use)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            ResultCard(label = "SHA-256 (256-bit)", value = sha256)
            ResultCard(label = "MD5 (128-bit)", value = md5)
            ResultCard(label = "SHA-1 (160-bit)", value = sha1)
            ResultCard(label = "SHA-512 (512-bit)", value = sha512)
            ResultCard(label = "Base64 Encoding", value = base64)
        }
    }
}
