package com.codexcraft.fileflow.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.codexcraft.fileflow.presentation.theme.DeepNavy
import com.codexcraft.fileflow.presentation.theme.IceBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToDashboard: () -> Unit
) {
    var animationPhase by remember { mutableStateOf(0) }
    
    val pathProgress by animateFloatAsState(
        targetValue = if (animationPhase >= 1) 1f else 0f,
        animationSpec = tween(1000, easing = EaseInOut),
        label = "pathDraw"
    )
    
    val logoScale by animateFloatAsState(
        targetValue = if (animationPhase >= 2) 0.3f else 1f,
        animationSpec = tween(400, easing = EaseInOutCubic),
        label = "scaleDown"
    )
    
    val offsetX by animateDpAsState(
        targetValue = if (animationPhase >= 3) (-120).dp else 0.dp,
        animationSpec = tween(400, easing = EaseInOutCubic),
        label = "slideX"
    )
    
    val offsetY by animateDpAsState(
        targetValue = if (animationPhase >= 3) (-280).dp else 0.dp,
        animationSpec = tween(400, easing = EaseInOutCubic),
        label = "slideY"
    )

    LaunchedEffect(Unit) {
        delay(300)
        animationPhase = 1
        delay(1000)
        animationPhase = 2
        delay(400)
        animationPhase = 3
        delay(400)
        onNavigateToDashboard()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavy),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetX, y = offsetY)
                .scale(logoScale)
        ) {
            FlowLogo(progress = pathProgress)
        }
    }
}

@Composable
fun FlowLogo(progress: Float) {
    Canvas(
        modifier = Modifier.size(200.dp)
    ) {
        val path = Path().apply {
            moveTo(50f, 100f)
            cubicTo(75f, 50f, 125f, 50f, 150f, 100f)
            cubicTo(175f, 150f, 225f, 150f, 250f, 100f)
        }
        
        drawPath(
            path = path,
            color = IceBlue,
            style = Stroke(
                width = 12f,
                cap = StrokeCap.Round
            ),
            alpha = progress
        )
    }
}
