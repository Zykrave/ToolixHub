package com.zykrave.toolixhub.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ToolixCyan,
    onPrimary = Color(0xFF031A24),
    primaryContainer = ToolixCyanContainer,
    onPrimaryContainer = ToolixOnCyanContainer,
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = ToolixAmber,
    onTertiary = Color(0xFF451A03),
    background = ToolixCharcoalDark,
    onBackground = Color(0xFFF0F6FC),
    surface = ToolixSurfaceDark,
    onSurface = Color(0xFFF0F6FC),
    surfaceVariant = ToolixSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFC9D1D9),
    outline = ToolixBorderDark,
    outlineVariant = Color(0xFF21262D)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ToolixPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = ToolixPrimaryContainerLight,
    onPrimaryContainer = ToolixOnPrimaryContainerLight,
    secondary = Color(0xFF64748B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF1E293B),
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = ToolixBackgroundLight,
    onBackground = Color(0xFF0F172A),
    surface = ToolixSurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = ToolixSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF475569),
    outline = ToolixBorderLight,
    outlineVariant = Color(0xFFE2E8F0)
  )

@Composable
fun ToolixTheme(
  darkTheme: Boolean = true, // Default to dark theme as requested
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
