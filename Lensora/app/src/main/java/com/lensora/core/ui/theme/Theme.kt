package com.lensora.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Black = Color(0xFF080808)
val DarkGray = Color(0xFF121212)
val SurfaceGray = Color(0xFF1E1E1E)
val ElectricBlue = Color(0xFF4FC3F7)
val ElectricBlueDim = Color(0xFF1A5F7A)
val White = Color(0xFFFFFFFF)
val WhiteDim = Color(0xFFB0B0B0)
val ErrorRed = Color(0xFFCF6679)

private val LensoraColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = Black,
    secondary = ElectricBlueDim,
    onSecondary = White,
    background = Black,
    onBackground = White,
    surface = DarkGray,
    onSurface = White,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = WhiteDim,
    error = ErrorRed,
    onError = White
)

@Composable
fun LensoraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LensoraColorScheme,
        content = content
    )
}
