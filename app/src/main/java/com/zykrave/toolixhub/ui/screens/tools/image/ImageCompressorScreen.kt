package com.zykrave.toolixhub.ui.screens.tools.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.EmptyStateView
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.io.ByteArrayOutputStream

@Composable
fun ImageCompressorScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var originalSizeBytes by remember { mutableIntStateOf(0) }
    var quality by remember { mutableFloatStateOf(75f) }
    var compressedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var compressedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun compress(bitmap: Bitmap, q: Int) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, q, stream)
        val bytes = stream.toByteArray()
        compressedBytes = bytes
        compressedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val rawBytes = stream.readBytes()
                    originalSizeBytes = rawBytes.size
                    val bmp = BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size)
                    originalBitmap = bmp
                    bmp?.let { b -> compress(b, quality.toInt()) }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not open image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun formatFileSize(bytes: Int): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }

    ToolixToolScaffold(
        title = "Image Compressor",
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
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Select Image to Compress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Button(
                        onClick = {
                            photoPicker.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("pick_image_button")
                    ) {
                        Text(if (originalBitmap == null) "Pick Image from Gallery" else "Choose Different Image")
                    }

                    if (originalBitmap != null) {
                        Text("Compression Quality: ${quality.toInt()}%", fontWeight = FontWeight.Medium)
                        Slider(
                            value = quality,
                            onValueChange = {
                                quality = it
                                originalBitmap?.let { b -> compress(b, it.toInt()) }
                            },
                            valueRange = 10f..100f,
                            steps = 17,
                            modifier = Modifier.fillMaxWidth().testTag("quality_slider")
                        )
                    }
                }
            }

            if (originalBitmap == null) {
                EmptyStateView(
                    icon = Icons.Default.AddPhotoAlternate,
                    title = "No Image Selected",
                    subtitle = "Select an image to see real-time compression and file size comparison"
                )
            } else {
                compressedBitmap?.let { bmp ->
                    ToolixCard {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Preview Compressed Image", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Compressed Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                val originalStr = formatFileSize(originalSizeBytes)
                val compressedSize = compressedBytes?.size ?: 0
                val compressedStr = formatFileSize(compressedSize)
                val savings = if (originalSizeBytes > 0) {
                    ((originalSizeBytes - compressedSize).toDouble() / originalSizeBytes * 100).coerceAtLeast(0.0)
                } else 0.0

                ResultCard(
                    label = "Compressed Size",
                    value = compressedStr,
                    subtitle = "Original: $originalStr | Saved: ${String.format("%.1f", savings)}% reduction"
                )
            }
        }
    }
}
