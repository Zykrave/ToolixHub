package com.zykrave.toolixhub.ui.screens.tools.utility

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.ui.components.ToolixToolScaffold
import java.io.File
import java.text.DecimalFormat

@Composable
fun SystemInfoScreen(
    onBack: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current

    // Memory info
    val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memInfo = ActivityManager.MemoryInfo()
    actManager.getMemoryInfo(memInfo)
    val totalRamGb = memInfo.totalMem / (1024.0 * 1024.0 * 1024.0)
    val availRamGb = memInfo.availMem / (1024.0 * 1024.0 * 1024.0)

    // Storage info
    val statFs = StatFs(Environment.getDataDirectory().path)
    val totalStorageGb = (statFs.blockCountLong * statFs.blockSizeLong) / (1024.0 * 1024.0 * 1024.0)
    val availStorageGb = (statFs.availableBlocksLong * statFs.blockSizeLong) / (1024.0 * 1024.0 * 1024.0)

    // Display info
    val dm = context.resources.displayMetrics
    val screenRes = "${dm.widthPixels} x ${dm.heightPixels} px (${dm.densityDpi} dpi)"

    // Battery info
    var batteryPct by remember { mutableIntStateOf(100) }
    var isCharging by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                if (intent != null) {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    if (level >= 0 && scale > 0) {
                        batteryPct = (level * 100) / scale
                    }
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val df = DecimalFormat("#0.0 GB")

    ToolixToolScaffold(
        title = "System Info Dashboard",
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
            // Hardware Info
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Device & Hardware", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    InfoRow(label = "Model", value = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}")
                    InfoRow(label = "Brand / Board", value = "${Build.BRAND} (${Build.BOARD})")
                    InfoRow(label = "CPU Architectures", value = Build.SUPPORTED_ABIS.joinToString(", "))
                }
            }

            // OS & Software
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Operating System", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    InfoRow(label = "Android Version", value = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                    InfoRow(label = "Security Patch", value = Build.VERSION.SECURITY_PATCH)
                    InfoRow(label = "Build ID", value = Build.ID)
                }
            }

            // Display & Battery
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Display & Power", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    InfoRow(label = "Screen Resolution", value = screenRes)
                    InfoRow(label = "Battery Level", value = "$batteryPct% ${if (isCharging) "(Charging)" else "(Discharging)"}")
                }
            }

            // Memory & Storage
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Memory & Storage", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    InfoRow(label = "RAM Memory", value = "${df.format(availRamGb)} free of ${df.format(totalRamGb)}")
                    InfoRow(label = "Internal Storage", value = "${df.format(availStorageGb)} free of ${df.format(totalStorageGb)}")
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
