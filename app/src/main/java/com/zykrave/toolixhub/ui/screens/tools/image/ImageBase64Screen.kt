package com.zykrave.toolixhub.ui.screens.tools.image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
fun ImageBase64Screen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Image to Base64", "Base64 to Image")

    ToolixToolScaffold(
        title = "Image Base64 Converter",
        onBack = onBack,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedTab == 0) {
                    ImageToBase64View()
                } else {
                    Base64ToImageView()
                }
            }
        }
    }
}

@Composable
private fun ImageToBase64View() {
    val context = LocalContext.current
    var base64String by remember { mutableStateOf("") }
    var includeDataUriPrefix by remember { mutableStateOf(true) }
    var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val raw = stream.readBytes()
                    loadedBitmap = BitmapFactory.decodeByteArray(raw, 0, raw.size)
                    val encoded = Base64.encodeToString(raw, Base64.NO_WRAP)
                    base64String = encoded
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not convert image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val finalString = if (includeDataUriPrefix && base64String.isNotEmpty()) {
        "data:image/jpeg;base64,$base64String"
    } else {
        base64String
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ToolixCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Pick Image to Encode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Button(
                    onClick = {
                        photoPicker.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("pick_image_base64_button")
                ) {
                    Text(if (loadedBitmap == null) "Select Image from Gallery" else "Select Different Image")
                }

                if (base64String.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includeDataUriPrefix, onCheckedChange = { includeDataUriPrefix = it })
                        Text("Include 'data:image/jpeg;base64,' prefix")
                    }
                }
            }
        }

        if (base64String.isNotEmpty()) {
            loadedBitmap?.let { bmp ->
                ToolixCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Source Image (${bmp.width} × ${bmp.height} px)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Source",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            val previewStr = if (finalString.length > 200) finalString.take(200) + "..." else finalString
            ResultCard(
                label = "Base64 String (${finalString.length} characters)",
                value = previewStr,
                subtitle = "Click copy icon to copy complete ${finalString.length} chars to clipboard",
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Base64", finalString))
                    Toast.makeText(context, "Copied Base64 string to clipboard!", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            EmptyStateView(
                icon = Icons.Default.AddPhotoAlternate,
                title = "No Image Encoded",
                subtitle = "Select any picture to generate its raw Base64 string"
            )
        }
    }
}

@Composable
private fun Base64ToImageView() {
    val context = LocalContext.current
    var inputBase64 by remember { mutableStateOf("") }
    var decodedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun decode() {
        errorMessage = null
        decodedBitmap = null
        try {
            val clean = inputBase64.substringAfter("base64,").trim()
            if (clean.isBlank()) {
                errorMessage = "Please enter a base64 string."
                return
            }
            val bytes = Base64.decode(clean, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (bmp != null) {
                decodedBitmap = bmp
            } else {
                errorMessage = "Invalid image data in Base64 string."
            }
        } catch (e: Exception) {
            errorMessage = "Decode error: ${e.localizedMessage}"
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ToolixCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Paste Base64 Image Code", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = inputBase64,
                    onValueChange = { inputBase64 = it },
                    placeholder = { Text("data:image/png;base64,iVBORw0KGgo...") },
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                )

                Button(
                    onClick = { decode() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Decode & Render Image")
                }
            }
        }

        errorMessage?.let { err ->
            ToolixCard(borderColor = MaterialTheme.colorScheme.error) {
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        decodedBitmap?.let { bmp ->
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Decoded Image Result", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Decoded",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            ResultCard(
                label = "Rendered Dimensions",
                value = "${bmp.width} × ${bmp.height} px",
                subtitle = "Successfully reconstructed from Base64"
            )
        }
    }
}
