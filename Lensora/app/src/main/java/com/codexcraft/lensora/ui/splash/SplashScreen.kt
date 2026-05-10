package com.codexcraft.lensora.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.codexcraft.lensora.R
import com.codexcraft.lensora.core.theme.ElectricBlue
import com.codexcraft.lensora.core.theme.LensoraTypography
import com.codexcraft.lensora.core.theme.MatteBlack
import com.codexcraft.lensora.core.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    val fadeIn = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        fadeIn.animateTo(1f, tween(800, easing = LinearEasing))

        launch {
            glowAlpha.animateTo(
                targetValue = 0.9f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }

        delay(2000L)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MatteBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.glowEffect(ElectricBlue, glowAlpha.value * 28f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_lensora_logo),
                    contentDescription = "Lensora AI Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .alpha(fadeIn.value),
                    colorFilter = ColorFilter.tint(ElectricBlue)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "LENSORA AI",
                style = LensoraTypography.titleLarge.copy(
                    letterSpacing = 6.sp,
                    color = ElectricBlue
                ),
                modifier = Modifier.alpha(fadeIn.value)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "by CodexCraft",
                style = LensoraTypography.labelMedium,
                modifier = Modifier.alpha(fadeIn.value * 0.6f)
            )
        }
    }
}

fun Modifier.glowEffect(glowColor: Color, radius: Float): Modifier = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            asFrameworkPaint().apply {
                isAntiAlias = true
                color = android.graphics.Color.TRANSPARENT
                setShadowLayer(radius, 0f, 0f, glowColor.copy(alpha = 0.7f).toArgb())
            }
        }
        canvas.drawCircle(
            center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2),
            radius = size.minDimension / 2,
            paint = paint
        )
    }
}