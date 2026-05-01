package com.codexcraft.caretap.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CareTapColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    background = Background,
    onBackground = TextPrimary,
    surface = TileBackground,
    onSurface = TextPrimary,
)

@Composable
fun CareTapTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CareTapColorScheme,
        typography = Typography,
        content = content
    )
}