package com.example.qrforge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object QRForgeColors {
    val LightBackground    = Color(0xFFF5F3FF)
    val LightSurface       = Color(0xFFFFFFFF)
    val LightSurface2      = Color(0xFFF0EEFF)
    val LightPrimary       = Color(0xFF4B3FC7)
    val LightOnPrimary     = Color(0xFFFFFFFF)
    val LightOnBackground  = Color(0xFF1A1A2E)
    val LightSubtitle      = Color(0xFF6B6B8A)
    val LightDivider       = Color(0xFFE8E4FF)

    val DarkBackground     = Color(0xFF08080F)
    val DarkSurface        = Color(0xFF0F0F1A)
    val DarkSurface2       = Color(0xFF161625)
    val DarkPrimary        = Color(0xFF7C6FE0)
    val DarkOnPrimary      = Color(0xFF08080F)
    val DarkOnBackground   = Color(0xFFF0EEFF)
    val DarkSubtitle       = Color(0xFF8888AA)
    val DarkDivider        = Color(0xFF1E1E35)

    val AccentBlue         = Color(0xFF4F8EF7)
    val AccentOrange       = Color(0xFFFF8C42)
    val AccentTeal         = Color(0xFF00C9A7)
    val AccentPink         = Color(0xFFFF6B9D)
    val AccentPurple       = Color(0xFF9B59B6)
    val AccentGreen        = Color(0xFF27AE60)
    val AccentRed          = Color(0xFFE74C3C)
}

val qrTypeColors = mapOf(
    "Website" to QRForgeColors.AccentBlue,
    "Text"    to QRForgeColors.AccentOrange,
    "Email"   to QRForgeColors.AccentTeal,
    "SMS"     to QRForgeColors.AccentPink,
    "WiFi"    to QRForgeColors.AccentPurple,
    "Phone"   to QRForgeColors.AccentGreen,
    "Contact" to QRForgeColors.AccentRed,
)

private val LightColorScheme = lightColorScheme(
    primary            = QRForgeColors.LightPrimary,
    onPrimary          = QRForgeColors.LightOnPrimary,
    primaryContainer   = QRForgeColors.LightSurface2,
    background         = QRForgeColors.LightBackground,
    onBackground       = QRForgeColors.LightOnBackground,
    surface            = QRForgeColors.LightSurface,
    onSurface          = QRForgeColors.LightOnBackground,
    surfaceVariant     = QRForgeColors.LightSurface2,
    outline            = QRForgeColors.LightDivider,
)

private val DarkColorScheme = darkColorScheme(
    primary            = QRForgeColors.DarkPrimary,
    onPrimary          = QRForgeColors.DarkOnPrimary,
    primaryContainer   = QRForgeColors.DarkSurface2,
    background         = QRForgeColors.DarkBackground,
    onBackground       = QRForgeColors.DarkOnBackground,
    surface            = QRForgeColors.DarkSurface,
    onSurface          = QRForgeColors.DarkOnBackground,
    surfaceVariant     = QRForgeColors.DarkSurface2,
    outline            = QRForgeColors.DarkDivider,
)

val QRForgeTypography = Typography(
    displayLarge   = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp),
    headlineLarge  = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 26.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 22.sp),
    titleLarge     = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 18.sp),
    titleMedium    = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 16.sp),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 16.sp),
    bodyMedium     = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 14.sp),
    labelLarge     = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 14.sp),
    labelSmall     = TextStyle(fontWeight = FontWeight.Medium,    fontSize = 11.sp),
)

@Composable
fun QRForgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = QRForgeTypography,
        content     = content
    )
}