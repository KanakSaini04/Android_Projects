package com.codexcraft.lensora.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LensoraDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = MatteBlack,
    primaryContainer = ElectricBlueAlpha20,
    onPrimaryContainer = ElectricBlue,
    secondary = ElectricBlue,
    onSecondary = MatteBlack,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = TextPrimary,
    tertiary = SuccessGreen,
    background = MatteBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = TextMuted,
    outlineVariant = SurfaceElevated,
    error = DangerRed,
    onError = MatteBlack,
    scrim = Color(0xCC000000),
    inverseSurface = TextPrimary,
    inverseOnSurface = MatteBlack,
    inversePrimary = MatteBlack
)

@Composable
fun LensoraTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = LensoraDarkColorScheme,
        typography = LensoraTypography,
        content = content
    )
}