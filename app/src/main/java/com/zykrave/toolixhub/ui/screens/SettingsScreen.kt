package com.zykrave.toolixhub.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zykrave.toolixhub.BuildConfig
import com.zykrave.toolixhub.data.AppThemeMode
import com.zykrave.toolixhub.data.PreferencesManager
import com.zykrave.toolixhub.ui.components.ToolixCard
import com.zykrave.toolixhub.util.GitHubRelease
import com.zykrave.toolixhub.util.GitHubReleaseChecker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    prefs: PreferencesManager,
    themeMode: AppThemeMode,
    useDynamicColor: Boolean,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isCheckingForUpdate by remember { mutableStateOf(false) }
    var updateAvailable by remember { mutableStateOf<Boolean?>(null) }
    var latestRelease by remember { mutableStateOf<GitHubRelease?>(null) }
    var updateCheckError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Privacy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Section
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Appearance & Theme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    Text("Theme Mode", style = MaterialTheme.typography.bodyMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            AppThemeMode.DARK to "Dark",
                            AppThemeMode.LIGHT to "Light",
                            AppThemeMode.SYSTEM to "System"
                        ).forEach { (mode, label) ->
                            FilterChip(
                                selected = themeMode == mode,
                                onClick = {
                                    scope.launch { prefs.setThemeMode(mode) }
                                },
                                label = { Text(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Material You Dynamic Color", fontWeight = FontWeight.Medium)
                            Text("Adapt app palette to device wallpaper (Android 12+)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = useDynamicColor,
                            onCheckedChange = { checked ->
                                scope.launch { prefs.setDynamicColor(checked) }
                            }
                        )
                    }
                }
            }

            // Storage & Data management
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Local Data Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                prefs.clearRecents()
                                Toast.makeText(context, "Recent tools cleared", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Text(" Clear Recent Tools History")
                    }
                }
            }

            // Offline & Privacy Guarantee
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF4CAF50))
                        Text("Privacy Guarantee", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    }

                    Text(
                        text = "ToolixHub is an offline-first multitool. It contains ZERO ads, ZERO trackers, and ZERO analytics. Network access is used only for explicit software updates. Your images, texts, generated keys, and sensor telemetry never leave your physical device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // About
            ToolixCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ToolixHub Multitool", fontWeight = FontWeight.Bold)
                    Text("Made by Zykrave", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Version ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Crafted with Kotlin & Jetpack Compose Material 3", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                isCheckingForUpdate = true
                                updateCheckError = null
                                updateAvailable = null
                                latestRelease = null
                                val result = GitHubReleaseChecker.getLatestRelease()
                                result.onSuccess { release ->
                                    latestRelease = release
                                    updateAvailable = GitHubReleaseChecker.isNewerVersion(
                                        latestVersion = release.tagName,
                                        currentVersion = BuildConfig.VERSION_NAME
                                    )
                                    isCheckingForUpdate = false
                                }.onFailure {
                                    updateCheckError = "Unable to check for updates."
                                    isCheckingForUpdate = false
                                }
                            }
                        },
                        enabled = !isCheckingForUpdate,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isCheckingForUpdate) "Checking..." else "Check for Updates")
                    }

                    if (updateCheckError != null) {
                        Text(
                            text = updateCheckError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (updateAvailable == true && latestRelease != null) {
                        val release = latestRelease!!
                        Text(
                            text = "Update available: ${release.tagName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (!release.releaseName.isNullOrBlank()) {
                            Text(
                                text = release.releaseName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else if (updateAvailable == false) {
                        Text(
                            text = "You're up to date.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
