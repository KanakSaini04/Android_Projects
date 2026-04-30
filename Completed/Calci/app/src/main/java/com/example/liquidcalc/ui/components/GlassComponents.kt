package com.example.liquidcalc.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonType {
    NUMBER,
    OPERATOR,
    FUNCTION,
    EQUALS
}

@Composable
fun GlassButton(
    label: String,
    type: ButtonType,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 22.sp,
    cornerRadius: Dp = 28.dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isPressed) 0.7f else 1f,
        animationSpec = tween(durationMillis = 80),
        label = "alpha"
    )

    val shape = RoundedCornerShape(cornerRadius)

    val bgBrush: Brush
    val textColor: Color
    val borderColor: Color

    when (type) {
        ButtonType.NUMBER -> {
            bgBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.22f),
                    Color.White.copy(alpha = 0.10f)
                ),
                start = Offset(0f, 0f),
                end = Offset(200f, 200f)
            )
            textColor = Color.White
            borderColor = Color.White.copy(alpha = 0.30f)
        }
        ButtonType.FUNCTION -> {
            bgBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.18f)
                ),
                start = Offset(0f, 0f),
                end = Offset(200f, 200f)
            )
            textColor = Color.White
            borderColor = Color.White.copy(alpha = 0.50f)
        }
        ButtonType.OPERATOR -> {
            bgBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFF9F0A).copy(alpha = 0.90f),
                    Color(0xFFFF6B00).copy(alpha = 0.80f)
                ),
                start = Offset(0f, 0f),
                end = Offset(200f, 200f)
            )
            textColor = Color.White
            borderColor = Color(0xFFFFBF60).copy(alpha = 0.60f)
        }
        ButtonType.EQUALS -> {
            bgBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFF9F0A),
                    Color(0xFFFF6B00)
                ),
                start = Offset(0f, 0f),
                end = Offset(200f, 200f)
            )
            textColor = Color.White
            borderColor = Color(0xFFFFBF60).copy(alpha = 0.60f)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clip(shape)
            .background(brush = bgBrush)
            .border(
                width = 0.8.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor,
                        Color.Transparent,
                        borderColor.copy(alpha = borderColor.alpha * 0.3f)
                    )
                ),
                shape = shape
            )
            .drawBehind {
                drawLine(
                    color = Color.White.copy(
                        alpha = if (type == ButtonType.NUMBER) 0.35f else 0.20f
                    ),
                    start = Offset(size.width * 0.15f, 2f),
                    end = Offset(size.width * 0.85f, 2f),
                    strokeWidth = 1.5f
                )
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = if (type == ButtonType.OPERATOR || type == ButtonType.EQUALS)
                FontWeight.Bold else FontWeight.SemiBold,
            color = textColor,
            letterSpacing = (-0.5).sp
        )
    }
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 32.dp,
    backgroundAlpha: Float = 0.40f,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = backgroundAlpha + 0.08f),
                        Color.White.copy(alpha = backgroundAlpha)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(600f, 600f)
                )
            )
            .border(
                width = 0.8.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.50f),
                        Color.White.copy(alpha = 0.10f),
                        Color.White.copy(alpha = 0.25f)
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}