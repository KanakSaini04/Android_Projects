package com.example.liquidcalc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF9F0A),
    onPrimary = Color.White,
    secondary = Color(0xFFFF6B00),
    onSecondary = Color.White,
    background = Color(0xFF1C1C1E),
    onBackground = Color.White,
    surface = Color(0xFF2C2C2E),
    onSurface = Color.White,
)

@Composable
fun LiquidCalcTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}