package com.zykrave.toolixhub.ui.screens.tools.utility

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@Composable
fun QrGeneratorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var qrType by remember { mutableIntStateOf(0) } // 0: Text/URL, 1: Wi-Fi, 2: Email
    var textInput by remember { mutableStateOf("https://github.com") }

    // Wi-Fi inputs
    var wifiSsid by remember { mutableStateOf("MyHomeNetwork") }
    var wifiPass by remember { mutableStateOf("secret123") }

    val rawContent = when (qrType) {
        0 -> textInput
        1 -> "WIFI:S:$wifiSsid;T:WPA;P:$wifiPass;;"
        else -> "mailto:$textInput"
    }

    fun generateQrBitmap(content: String, size: Int = 512): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
            val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bmp
        } catch (e: Exception) {
            null
        }
    }

    val qrBitmap = remember(rawContent) { generateQrBitmap(rawContent) }
    val context = LocalContext.current

    ToolixToolScaffold(
        title = "QR Code Generator",
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // QR Code display
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.size(240.dp).padding(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "Generated QR Code",
                            modifier = Modifier.fillMaxSize().padding(12.dp)
                        )
                    } else {
                        Text("Enter text to generate QR", color = Color.Gray)
                    }
                }
            }

            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("QR Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Text / URL", "Wi-Fi", "Email").forEachIndexed { idx, label ->
                            FilterChip(
                                selected = qrType == idx,
                                onClick = { qrType = idx },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (qrType == 0 || qrType == 2) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            label = { Text(if (qrType == 0) "URL or Text" else "Email Address") },
                            modifier = Modifier.fillMaxWidth().testTag("qr_text_input")
                        )
                    } else {
                        OutlinedTextField(
                            value = wifiSsid,
                            onValueChange = { wifiSsid = it },
                            label = { Text("Network Name (SSID)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = wifiPass,
                            onValueChange = { wifiPass = it },
                            label = { Text("Wi-Fi Password") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            ResultCard(
                label = "Encoded Payload",
                value = rawContent,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("QR Payload", rawContent))
                    Toast.makeText(context, "Copied payload to clipboard!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
