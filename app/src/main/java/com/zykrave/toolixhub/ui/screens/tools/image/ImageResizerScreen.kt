package com.zykrave.toolixhub.ui.screens.tools.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.EmptyStateView
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold

@Composable
fun ImageResizerScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var scalePercent by remember { mutableFloatStateOf(50f) }
    var lockRatio by remember { mutableStateOf(true) }
    var widthInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }

    var resizedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun updateResized(newW: Int, newH: Int) {
        val orig = originalBitmap ?: return
        if (newW > 0 && newH > 0 && newW <= 4096 && newH <= 4096) {
            try {
                resizedBitmap = Bitmap.createScaledBitmap(orig, newW, newH, true)
            } catch (e: OutOfMemoryError) {
                Toast.makeText(context, "Image dimensions too large for memory", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    originalBitmap = bmp
                    bmp?.let { b ->
                        val targetW = (b.width * (scalePercent / 100f)).toInt().coerceAtLeast(1)
                        val targetH = (b.height * (scalePercent / 100f)).toInt().coerceAtLeast(1)
                        widthInput = targetW.toString()
                        heightInput = targetH.toString()
                        updateResized(targetW, targetH)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not open image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    ToolixToolScaffold(
        title = "Image Resizer",
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
                    Text("Select Image to Resize", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Button(
                        onClick = {
                            photoPicker.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("pick_image_resizer_button")
                    ) {
                        Text(if (originalBitmap == null) "Pick Image from Gallery" else "Choose Different Image")
                    }

                    originalBitmap?.let { orig ->
                        Text("Original Dimensions: ${orig.width} × ${orig.height} px", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)

                        Text("Scale by Percentage: ${scalePercent.toInt()}%", fontWeight = FontWeight.Medium)
                        Slider(
                            value = scalePercent,
                            onValueChange = {
                                scalePercent = it
                                val newW = (orig.width * (it / 100f)).toInt().coerceAtLeast(1)
                                val newH = (orig.height * (it / 100f)).toInt().coerceAtLeast(1)
                                widthInput = newW.toString()
                                heightInput = newH.toString()
                                updateResized(newW, newH)
                            },
                            valueRange = 10f..200f,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = widthInput,
                                onValueChange = {
                                    widthInput = it
                                    val w = it.toIntOrNull() ?: 0
                                    if (lockRatio && orig.width > 0) {
                                        val h = ((w.toDouble() / orig.width) * orig.height).toInt()
                                        heightInput = h.toString()
                                        updateResized(w, h)
                                    } else {
                                        val h = heightInput.toIntOrNull() ?: 0
                                        updateResized(w, h)
                                    }
                                },
                                label = { Text("Width (px)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = heightInput,
                                onValueChange = {
                                    heightInput = it
                                    val h = it.toIntOrNull() ?: 0
                                    if (lockRatio && orig.height > 0) {
                                        val w = ((h.toDouble() / orig.height) * orig.width).toInt()
                                        widthInput = w.toString()
                                        updateResized(w, h)
                                    } else {
                                        val w = widthInput.toIntOrNull() ?: 0
                                        updateResized(w, h)
                                    }
                                },
                                label = { Text("Height (px)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = lockRatio, onCheckedChange = { lockRatio = it })
                            Text("Lock Aspect Ratio")
                        }
                    }
                }
            }

            if (originalBitmap == null) {
                EmptyStateView(
                    icon = Icons.Default.AddPhotoAlternate,
                    title = "No Image Selected",
                    subtitle = "Pick an image to resize by resolution pixels or relative scale"
                )
            } else {
                resizedBitmap?.let { bmp ->
                    ToolixCard {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Resized Image Preview (${bmp.width} × ${bmp.height} px)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Resized Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    ResultCard(
                        label = "New Resolution",
                        value = "${bmp.width} × ${bmp.height} px",
                        subtitle = "Scaled from ${originalBitmap?.width} × ${originalBitmap?.height} px"
                    )
                }
            }
        }
    }
}
