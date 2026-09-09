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

@Composable
fun ImageCropperScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedRatioIdx by remember { mutableIntStateOf(0) }
    val ratios = listOf("Original", "1:1 Square", "4:3 Standard", "16:9 Widescreen", "3:2 Photo")

    var croppedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    fun performCrop(bmp: Bitmap, ratioIndex: Int) {
        val w = bmp.width
        val h = bmp.height

        if (ratioIndex == 0) {
            croppedBitmap = bmp
            return
        }

        val targetRatio = when (ratioIndex) {
            1 -> 1.0 / 1.0
            2 -> 4.0 / 3.0
            3 -> 16.0 / 9.0
            else -> 3.0 / 2.0
        }

        val currentRatio = w.toDouble() / h.toDouble()

        var cropW = w
        var cropH = h
        var startX = 0
        var startY = 0

        if (currentRatio > targetRatio) {
            cropW = (h * targetRatio).toInt().coerceAtMost(w)
            startX = (w - cropW) / 2
        } else {
            cropH = (w / targetRatio).toInt().coerceAtMost(h)
            startY = (h - cropH) / 2
        }

        try {
            croppedBitmap = Bitmap.createBitmap(bmp, startX, startY, cropW, cropH)
        } catch (e: Exception) {
            Toast.makeText(context, "Error cropping image", Toast.LENGTH_SHORT).show()
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
                    bmp?.let { b -> performCrop(b, selectedRatioIdx) }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not open image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    ToolixToolScaffold(
        title = "Basic Image Cropper",
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
                    Text("Select Image to Crop", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    Button(
                        onClick = {
                            photoPicker.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("pick_image_crop_button")
                    ) {
                        Text(if (originalBitmap == null) "Pick Image from Gallery" else "Choose Different Image")
                    }

                    if (originalBitmap != null) {
                        Text("Aspect Ratio", style = MaterialTheme.typography.labelMedium)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ratios.take(3).forEachIndexed { index, label ->
                                FilterChip(
                                    selected = selectedRatioIdx == index,
                                    onClick = {
                                        selectedRatioIdx = index
                                        originalBitmap?.let { b -> performCrop(b, index) }
                                    },
                                    label = { Text(label) }
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ratios.drop(3).forEachIndexed { index, label ->
                                val actualIdx = index + 3
                                FilterChip(
                                    selected = selectedRatioIdx == actualIdx,
                                    onClick = {
                                        selectedRatioIdx = actualIdx
                                        originalBitmap?.let { b -> performCrop(b, actualIdx) }
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
                    subtitle = "Select an image to crop to standard aspect ratios (1:1, 4:3, 16:9)"
                )
            } else {
                croppedBitmap?.let { bmp ->
                    ToolixCard {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Cropped Output Preview", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "Cropped Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    ResultCard(
                        label = "Cropped Resolution",
                        value = "${bmp.width} × ${bmp.height} px",
                        subtitle = "Aspect ratio: ${ratios[selectedRatioIdx]} (Original: ${originalBitmap?.width} × ${originalBitmap?.height} px)"
                    )
                }
            }
        }
    }
}
