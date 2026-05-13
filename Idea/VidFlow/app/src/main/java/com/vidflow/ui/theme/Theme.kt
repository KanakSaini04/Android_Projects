package com.vidflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme { LIGHT, DARK, SYSTEM }

private val DarkColors = darkColorScheme(
    primary = Green400,
    onPrimary = Dark900,
    primaryContainer = Green900,
    onPrimaryContainer = Green400,
    background = Dark900,
    onBackground = TextPrimary,
    surface = Dark700,
    onSurface = TextPrimary,
    surfaceVariant = Dark600,
    onSurfaceVariant = TextSecondary,
    outline = Dark500,
    error = ErrorRed
)

private val LightColors = lightColorScheme(
    primary = Green500,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Green900,
    background = Light100,
    onBackground = Color(0xFF111111),
    surface = Color.White,
    onSurface = Color(0xFF111111),
    surfaceVariant = Light200,
    onSurfaceVariant = Color(0xFF6B7280),
    outline = Light300,
    error = ErrorRed
)

@Composable
fun VidFlowTheme(appTheme: AppTheme = AppTheme.SYSTEM, content: @Composable () -> Unit) {
    val dark = when (appTheme) {
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}