package com.codexcraft.lensora.ui.edit

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codexcraft.lensora.core.theme.*

@Composable
fun EditScreen(
    viewModel: EditViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initializeTfLiteModels(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack)
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "THE LAB",
                style = LensoraTypography.labelLarge.copy(letterSpacing = 4.sp)
            )
            Spacer(Modifier.width(12.dp))
            Box(Modifier.height(1.dp).weight(1f).background(ElectricBlueAlpha20))
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Generative AI tools, on-device.",
            style = LensoraTypography.bodyMedium
        )

        Spacer(Modifier.height(32.dp))

        // Model status banner
        if (state.isLoading) {
            LoadingBanner()
        } else if (state.errorMessage != null) {
            StatusBanner(
                message = state.errorMessage!!,
                isError = true
            )
        }

        Spacer(Modifier.height(24.dp))

        // Magic Eraser tool
        EditToolCard(
            icon = Icons.Outlined.AutoFixHigh,
            title = "Magic Eraser",
            subtitle = "Generative Inpainting",
            description = "Remove any object from your photo. Powered by on-device diffusion. Tap a region to erase — the AI fills it seamlessly.",
            isReady = state.magicEraserReady,
            accentColor = ElectricBlue,
            features = listOf("Object removal", "Background fill", "Texture synthesis")
        )

        Spacer(Modifier.height(20.dp))

        // Relight AI tool
        EditToolCard(
            icon = Icons.Outlined.LightMode,
            title = "Relight AI",
            subtitle = "3D Depth Lighting",
            description = "Recompose the lighting in your scene using estimated depth maps. Drag the light source to any angle — results in real-time.",
            isReady = state.relightReady,
            accentColor = Color(0xFFFFAB40),
            features = listOf("Depth estimation", "Virtual point light", "Ambient override")
        )

        Spacer(Modifier.height(32.dp))

        // Coming soon section
        ComingSoonSection()

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun LoadingBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "banner_alpha"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ElectricBlueAlpha10, RoundedCornerShape(8.dp))
            .border(1.dp, ElectricBlue.copy(alpha = alpha), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = ElectricBlue,
            strokeWidth = 2.dp
        )
        Spacer(Modifier.width(12.dp))
        Text(
            "Initializing AI engines…",
            style = LensoraTypography.bodyMedium.copy(color = ElectricBlue)
        )
    }
}

@Composable
private fun StatusBanner(message: String, isError: Boolean) {
    val color = if (isError) DangerRed else SuccessGreen
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isError) Icons.Outlined.Info else Icons.Outlined.CheckCircle,            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(message, style = LensoraTypography.bodyMedium.copy(color = color))
    }
}

@Composable
private fun EditToolCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    description: String,
    isReady: Boolean,
    accentColor: Color,
    features: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceCard, RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (isReady) accentColor.copy(0.4f) else TextMuted.copy(0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accentColor.copy(0.1f), RoundedCornerShape(10.dp))
                    .border(1.dp, accentColor.copy(0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, style = LensoraTypography.titleSmall)
                Text(subtitle, style = LensoraTypography.labelSmall.copy(color = accentColor))
            }
            Spacer(Modifier.weight(1f))
            ReadinessBadge(isReady = isReady, color = accentColor)
        }

        Spacer(Modifier.height(14.dp))
        Text(description, style = LensoraTypography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        // Feature chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            features.forEach { feature ->
                Box(
                    modifier = Modifier
                        .background(accentColor.copy(0.08f), RoundedCornerShape(4.dp))
                        .border(1.dp, accentColor.copy(0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(feature, style = LensoraTypography.labelSmall.copy(color = accentColor, fontSize = 9.sp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Mock canvas preview area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(SurfaceElevated, SurfaceDark)
                    ),
                    RoundedCornerShape(8.dp)
                )
                .border(1.dp, TextMuted.copy(0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!isReady) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.CloudDownload, null, tint = TextMuted, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Place ${title.lowercase().replace(" ", "_")}.tflite in assets/",
                        style = LensoraTypography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    "Select a photo to begin",
                    style = LensoraTypography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ReadinessBadge(isReady: Boolean, color: Color) {
    Row(
        modifier = Modifier
            .background(
                if (isReady) color.copy(0.1f) else TextMuted.copy(0.1f),
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(5.dp)
                .background(if (isReady) color else TextMuted, RoundedCornerShape(50))
        )
        Spacer(Modifier.width(5.dp))
        Text(
            if (isReady) "READY" else "OFFLINE",
            style = LensoraTypography.labelSmall.copy(
                color = if (isReady) color else TextMuted,
                fontSize = 9.sp
            )
        )
    }
}

@Composable
private fun ComingSoonSection() {
    Column {
        Text(
            "COMING NEXT",
            style = LensoraTypography.labelLarge.copy(letterSpacing = 3.sp, color = TextMuted)
        )
        Spacer(Modifier.height(12.dp))
        listOf(
            "Style Transfer — Apply cinematic LUT looks",
            "Sky Swap — AI-powered sky replacement",
            "Motion Blur FX — Directional blur synthesis"
        ).forEach { item ->
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(4.dp).background(TextMuted, RoundedCornerShape(50)))
                Spacer(Modifier.width(10.dp))
                Text(item, style = LensoraTypography.bodyMedium)
            }
        }
    }
}