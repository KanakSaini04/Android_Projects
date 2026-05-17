package com.codexcraft.fileflow.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = IceBlue,
    onPrimary = DeepNavy,
    primaryContainer = SteelBlue,
    onPrimaryContainer = IceBlue,
    secondary = ArcticCyan,
    onSecondary = DeepNavy,
    background = SurfaceDark,
    onBackground = FrostWhite,
    surface = DeepNavy,
    onSurface = FrostWhite,
    surfaceVariant = CharcoalGray,
    onSurfaceVariant = IceBlue,
    error = DangerRed,
    onError = FrostWhite
)

private val LightColorScheme = lightColorScheme(
    primary = DeepNavy,
    onPrimary = FrostWhite,
    primaryContainer = IceBlue,
    onPrimaryContainer = DeepNavy,
    secondary = SteelBlue,
    onSecondary = FrostWhite,
    background = FrostWhite,
    onBackground = CharcoalGray,
    surface = SurfaceLight,
    onSurface = CharcoalGray,
    surfaceVariant = IceBlue,
    onSurfaceVariant = DeepNavy,
    error = DangerRed,
    onError = FrostWhite
)

@Composable
fun FileFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FileFlowTypography,
        content = content
    )
}
