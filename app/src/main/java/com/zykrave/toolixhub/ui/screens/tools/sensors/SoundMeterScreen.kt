package com.zykrave.toolixhub.ui.screens.tools.sensors

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.zykrave.toolixhub.ui.components.ResultCard
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.sqrt

@Composable
fun SoundMeterScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    var isRecording by remember { mutableStateOf(false) }
    var currentDb by remember { mutableFloatStateOf(0f) }
    var peakDb by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isRecording, hasPermission) {
        if (!isRecording || !hasPermission) return@LaunchedEffect

        withContext(Dispatchers.IO) {
            val sampleRate = 44100
            val channelConfig = AudioFormat.CHANNEL_IN_MONO
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = max(minBufSize, 2048)
            val buffer = ShortArray(bufferSize)

            try {
                val recorder = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSize
                )

                if (recorder.state == AudioRecord.STATE_INITIALIZED) {
                    recorder.startRecording()

                    while (isActive && isRecording) {
                        val read = recorder.read(buffer, 0, bufferSize)
                        if (read > 0) {
                            var sum = 0.0
                            for (i in 0 until read) {
                                sum += buffer[i] * buffer[i]
                            }
                            val rms = sqrt(sum / read)
                            // Standard calibration approx: 20 * log10(rms)
                            val db = if (rms > 0) (20 * log10(rms)).toFloat() else 0f
                            val calibratedDb = (db * 1.5f).coerceIn(20f, 120f)

                            withContext(Dispatchers.Main) {
                                currentDb = calibratedDb
                                if (calibratedDb > peakDb) {
                                    peakDb = calibratedDb
                                }
                            }
                        }
                    }

                    recorder.stop()
                    recorder.release()
                }
            } catch (e: SecurityException) {
                // Permission revoked
            } catch (e: Exception) {
                // Hardware error
            }
        }
    }

    val environmentDesc = when (currentDb.toInt()) {
        in 0..30 -> "Quiet Whispering / Studio"
        in 31..50 -> "Quiet Library / Moderate Home"
        in 51..70 -> "Normal Conversation"
        in 71..85 -> "Busy Street Traffic / Restaurant"
        in 86..100 -> "Lawn Mower / Heavy Machinery"
        else -> "Rock Concert / Pain Threshold"
    }

    val dbRatio = ((currentDb - 20f) / 100f).coerceIn(0f, 1f)
    val gaugeColor = when {
        currentDb < 60 -> Color(0xFF4CAF50)
        currentDb < 85 -> Color(0xFFFFA000)
        else -> MaterialTheme.colorScheme.error
    }

    ToolixToolScaffold(
        title = "Sound & Decibel Meter",
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
            if (!hasPermission) {
                ToolixCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Microphone Permission Required", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "To measure ambient sound levels (dB), ToolixHub needs local access to your microphone. Audio is analyzed in memory only and never saved or transmitted.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                            modifier = Modifier.fillMaxWidth().testTag("grant_audio_permission_button")
                        ) {
                            Text("Grant Permission")
                        }
                    }
                }
            } else {
                ResultCard(
                    label = "Real-time Noise Level",
                    value = "${currentDb.toInt()} dB",
                    subtitle = environmentDesc,
                    accentColor = gaugeColor
                )

                LinearProgressIndicator(
                    progress = { dbRatio },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = gaugeColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResultCard(
                        label = "Peak Recorded",
                        value = "${peakDb.toInt()} dB",
                        modifier = Modifier.weight(1f)
                    )
                    ResultCard(
                        label = "Status",
                        value = if (isRecording) "Listening..." else "Paused",
                        modifier = Modifier.weight(1f)
                    )
                }

                ToolixCard {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { isRecording = !isRecording },
                            modifier = Modifier.fillMaxWidth().testTag("toggle_sound_meter_button")
                        ) {
                            Text(if (isRecording) "Pause Metering" else "Start Metering")
                        }

                        if (peakDb > 0) {
                            OutlinedButton(
                                onClick = { peakDb = 0f },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Reset Peak")
                            }
                        }
                    }
                }
            }
        }
    }
}
