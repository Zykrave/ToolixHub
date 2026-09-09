package com.zykrave.toolixhub.ui.screens.tools.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun ImageFormatConverterScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var originalSizeBytes by remember { mutableIntStateOf(0) }
    var selectedFormatIndex by remember { mutableIntStateOf(0) } // 0: JPEG, 1: PNG, 2: WEBP
    val formats = listOf("JPEG", "PNG", "WEBP")

    var convertedBytes by remember { mutableStateOf<ByteArray?>(null) }
    var convertedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun convertFormat(bitmap: Bitmap, formatIdx: Int) {
        val stream = ByteArrayOutputStream()
        val format = when (formatIdx) {
            0 -> Bitmap.CompressFormat.JPEG
            1 -> Bitmap.CompressFormat.PNG
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    @Suppress("DEPRECATION")
                    Bitmap.CompressFormat.WEBP
                }
            }
        }
        bitmap.compress(format, 90, stream)
        val bytes = stream.toByteArray()
        convertedBytes = bytes
        convertedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val raw = stream.readBytes()
                    originalSizeBytes = raw.size
                    val bmp = BitmapFactory.decodeByteArray(raw, 0, raw.size)
                    originalBitmap = bmp
                    bmp?.let { b -> convertFormat(b, selectedFormatIndex) }
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
        title = "Image Format Converter",
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
                    Text("Select Image to Convert", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Button(
                        onClick = {
                            photoPicker.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("pick_image_format_button")
                    ) {
                        Text(if (originalBitmap == null) "Pick Image from Gallery" else "Choose Different Image")
                    }

                    if (originalBitmap != null) {
                        Text("Target Format", style = MaterialTheme.typography.labelMedium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            formats.forEachIndexed { index, label ->
                                FilterChip(
                                    selected = selectedFormatIndex == index,
                                    onClick = {
                                        selectedFormatIndex = index
                                        originalBitmap?.let { b -> convertFormat(b, index) }
                                    },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                }
            }

            if (originalBitmap == null) {
                EmptyStateView(
                    icon = Icons.Default.AddPhotoAlternate,
                    title = "No Image Selected",
                    subtitle = "Pick an image to convert its container format between JPEG, PNG, and WebP"
                )
            } else {
                convertedBitmap?.let { bmp ->
                    ToolixCard {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Converted Output Preview (${formats[selectedFormatIndex]})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Converted image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    val targetSize = convertedBytes?.size ?: 0
                    ResultCard(
                        label = "Converted Format: ${formats[selectedFormatIndex]}",
                        value = formatFileSize(targetSize),
                        subtitle = "Original format size: ${formatFileSize(originalSizeBytes)}"
                    )
                }
            }
        }
    }
}
