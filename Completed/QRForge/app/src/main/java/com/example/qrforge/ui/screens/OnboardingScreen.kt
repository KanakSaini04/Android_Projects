package com.example.qrforge.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnboardingPage(
    val icon: ImageVector,
    val iconColor: Color,
    val title: String,
    val subtitle: String,
    val bgColor: Color
)

val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Outlined.QrCodeScanner,
        iconColor = Color(0xFF4FFFB0),
        title = "Scan Any Code",
        subtitle = "Instantly scan QR codes and barcodes using your camera or pick an image from your gallery. Supports 10+ formats.",
        bgColor = Color(0xFF0D1F1A)
    ),
    OnboardingPage(
        icon = Icons.Outlined.QrCode,
        iconColor = Color(0xFF7C6FE0),
        title = "Create QR Codes",
        subtitle = "Generate beautiful QR codes for URLs, WiFi, contacts, emails, SMS and more. Customize colors and export in high quality.",
        bgColor = Color(0xFF0F0D1F)
    ),
    OnboardingPage(
        icon = Icons.Outlined.History,
        iconColor = Color(0xFFFF8C42),
        title = "Track Everything",
        subtitle = "Every scan and generated code is saved to your history. Search, filter, favorite and export your scan history anytime.",
        bgColor = Color(0xFF1F140D)
    )
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    val page = onboardingPages[currentPage]

    Box(
        Modifier.fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(page.bgColor, Color(0xFF0A0A0A))
                )
            )
    ) {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip button
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                if (currentPage < onboardingPages.size - 1) {
                    TextButton(onClick = onComplete) {
                        Text("Skip", color = Color.White.copy(0.4f), fontSize = 14.sp)
                    }
                }
            }

            // Icon and content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Animated icon box
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        fadeIn(tween(400)) + scaleIn(tween(400)) togetherWith
                                fadeOut(tween(400)) + scaleOut(tween(400))
                    },
                    label = "icon"
                ) { pageIndex ->
                    val p = onboardingPages[pageIndex]
                    Box(
                        Modifier.size(160.dp).clip(RoundedCornerShape(40.dp))
                            .background(p.iconColor.copy(0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            p.icon, null,
                            tint = p.iconColor,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                // Text content
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        slideInHorizontally(tween(400)) { it } + fadeIn(tween(400)) togetherWith
                                slideOutHorizontally(tween(400)) { -it } + fadeOut(tween(400))
                    },
                    label = "text"
                ) { pageIndex ->
                    val p = onboardingPages[pageIndex]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            p.title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            p.subtitle,
                            fontSize = 15.sp,
                            color = Color.White.copy(0.5f),
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // Bottom section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Page indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    onboardingPages.forEachIndexed { index, _ ->
                        val isSelected = index == currentPage
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            animationSpec = tween(300),
                            label = "indicator"
                        )
                        Box(
                            Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) page.iconColor
                                    else Color.White.copy(0.2f)
                                )
                        )
                    }
                }

                // Next / Get Started button
                Button(
                    onClick = {
                        if (currentPage < onboardingPages.size - 1) {
                            currentPage++
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = page.iconColor
                    )
                ) {
                    Text(
                        if (currentPage < onboardingPages.size - 1) "Next →"
                        else "Get Started",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0A0A0A)
                    )
                }
            }
        }
    }
}