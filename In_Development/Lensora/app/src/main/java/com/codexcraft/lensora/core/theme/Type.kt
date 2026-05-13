package com.codexcraft.lensora.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using system sans-serif as fallback; replace with downloaded font assets for production
val LensoraFontFamily = FontFamily.SansSerif

val LensoraTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Thin,
        fontSize = 57.sp,
        letterSpacing = (-0.25).sp,
        color = TextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    headlineLarge = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 32.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 28.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        letterSpacing = 0.sp,
        color = TextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp,
        color = TextPrimary
    ),
    titleSmall = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        color = TextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp,
        color = TextSecondary
    ),
    bodySmall = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        color = TextMuted
    ),
    labelLarge = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.sp,
        color = ElectricBlue
    ),
    labelMedium = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.5.sp,
        color = TextSecondary
    ),
    labelSmall = TextStyle(
        fontFamily = LensoraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp,
        color = TextMuted
    )
)