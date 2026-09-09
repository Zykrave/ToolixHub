package com.zykrave.toolixhub.ui.screens.tools.random

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.security.SecureRandom

@Composable
fun PasswordGeneratorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var length by remember { mutableIntStateOf(16) }
    var useUpper by remember { mutableStateOf(true) }
    var useLower by remember { mutableStateOf(true) }
    var useDigits by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    var excludeAmbiguous by remember { mutableStateOf(false) }

    fun generatePassword(): String {
        var uppers = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var lowers = "abcdefghijklmnopqrstuvwxyz"
        var digits = "0123456789"
        var symbols = "!@#$%^&*()-_=+[]{}|;:,.<>?"

        if (excludeAmbiguous) {
            uppers = uppers.replace("I", "").replace("O", "")
            lowers = lowers.replace("l", "").replace("o", "")
            digits = digits.replace("1", "").replace("0", "")
            symbols = symbols.replace("|", "").replace(":", "")
        }

        val pool = StringBuilder()
        if (useUpper) pool.append(uppers)
        if (useLower) pool.append(lowers)
        if (useDigits) pool.append(digits)
        if (useSymbols) pool.append(symbols)

        if (pool.isEmpty()) return "Select at least 1 set"

        val random = SecureRandom()
        val result = StringBuilder()
        for (i in 0 until length) {
            val idx = random.nextInt(pool.length)
            result.append(pool[idx])
        }
        return result.toString()
    }

    var currentPassword by remember(length, useUpper, useLower, useDigits, useSymbols, excludeAmbiguous) {
        mutableStateOf(generatePassword())
    }

    // Password strength evaluation
    val poolSize = (if (useUpper) 26 else 0) + (if (useLower) 26 else 0) + (if (useDigits) 10 else 0) + (if (useSymbols) 30 else 0)
    val entropy = if (poolSize > 0) length * (kotlin.math.log2(poolSize.toDouble())) else 0.0
    val strengthRatio = (entropy / 100.0).coerceIn(0.1, 1.0).toFloat()
    val strengthLabel = when {
        entropy < 36 -> "Weak"
        entropy < 60 -> "Moderate"
        entropy < 80 -> "Strong"
        else -> "Very Strong"
    }
    val strengthColor = when {
        entropy < 36 -> MaterialTheme.colorScheme.error
        entropy < 60 -> Color(0xFFFFA000)
        else -> Color(0xFF4CAF50)
    }

    val context = LocalContext.current

    ToolixToolScaffold(
        title = "Password Generator",
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
            ResultCard(
                label = "Generated Password",
                value = currentPassword,
                subtitle = "Strength: $strengthLabel (${entropy.toInt()} bits entropy)",
                accentColor = strengthColor,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("ToolixHub Password", currentPassword))
                    Toast.makeText(context, "Password copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
            )

            LinearProgressIndicator(
                progress = { strengthRatio },
                modifier = Modifier.fillMaxWidth(),
                color = strengthColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Password Length: $length characters", fontWeight = FontWeight.SemiBold)
                    Slider(
                        value = length.toFloat(),
                        onValueChange = {
                            length = it.toInt()
                            currentPassword = generatePassword()
                        },
                        valueRange = 6f..40f,
                        steps = 33,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Character Sets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = useUpper, onCheckedChange = { useUpper = it })
                        Text("Uppercase Letters (A-Z)")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = useLower, onCheckedChange = { useLower = it })
                        Text("Lowercase Letters (a-z)")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = useDigits, onCheckedChange = { useDigits = it })
                        Text("Numbers (0-9)")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = useSymbols, onCheckedChange = { useSymbols = it })
                        Text("Special Symbols (!@#$%)")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = excludeAmbiguous, onCheckedChange = { excludeAmbiguous = it })
                        Text("Exclude Ambiguous Chars (1, l, I, 0, O)")
                    }

                    Button(
                        onClick = { currentPassword = generatePassword() },
                        modifier = Modifier.fillMaxWidth().testTag("regenerate_password_button")
                    ) {
                        Text("Regenerate Password")
                    }
                }
            }
        }
    }
}
