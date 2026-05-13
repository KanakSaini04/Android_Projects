package com.clustr.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Brand Colors ──────────────────────────────────────────────────────────────
val Black         = Color(0xFF000000)
val white = Color(0xDCFFFFFF)
val Surface800    = Color(0xFF0D0D0D)
val Surface700    = Color(0xFF141414)
val Surface600    = Color(0xFF1C1C1E)
val Surface500    = Color(0xFF2C2C2E)
val Surface400    = Color(0xFF3A3A3C)
val TextPrimary   = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF8E8E93)
val TextTertiary  = Color(0xFF48484A)
val Accent        = Color(0xCDFCFFFD)   // soft mint — visible on black, not harsh
val AccentDim     = Color(0xFF1A3D31)
val AccentRed     = Color(0xFFE70416)
val Divider       = Color(0xFF1C1C1E)

// ── Material3 Dark Color Scheme ───────────────────────────────────────────────
private val ClustrDarkScheme = darkColorScheme(
    primary          = Accent,
    onPrimary        = Black,
    primaryContainer = AccentDim,
    onPrimaryContainer = Accent,
    secondary        = TextSecondary,
    onSecondary      = Black,
    background       = Black,
    onBackground     = TextPrimary,
    surface          = Surface800,
    onSurface        = TextPrimary,
    surfaceVariant   = Surface600,
    onSurfaceVariant = TextSecondary,
    outline          = Surface500,
    outlineVariant   = Surface400,
    error            = AccentRed,
    onError          = Black,
)

@Composable
fun ClustrTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ClustrDarkScheme,
        typography  = ClustrTypography,
        content     = content
    )
}