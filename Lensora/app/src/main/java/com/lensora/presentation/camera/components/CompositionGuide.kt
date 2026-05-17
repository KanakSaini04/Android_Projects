package com.lensora.presentation.camera.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun CompositionGuide(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val thirdW = size.width / 3f
        val thirdH = size.height / 3f
        val color = Color.White.copy(alpha = 0.25f)
        val strokeWidth = 1f

        // Vertical lines
        drawLine(color, start = Offset(thirdW, 0f), end = Offset(thirdW, size.height), strokeWidth = strokeWidth)
        drawLine(color, start = Offset(thirdW * 2, 0f), end = Offset(thirdW * 2, size.height), strokeWidth = strokeWidth)

        // Horizontal lines
        drawLine(color, start = Offset(0f, thirdH), end = Offset(size.width, thirdH), strokeWidth = strokeWidth)
        drawLine(color, start = Offset(0f, thirdH * 2), end = Offset(size.width, thirdH * 2), strokeWidth = strokeWidth)

        // Center cross
        val cx = size.width / 2f
        val cy = size.height / 2f
        val crossSize = 20f
        drawLine(color, Offset(cx - crossSize, cy), Offset(cx + crossSize, cy), strokeWidth)
        drawLine(color, Offset(cx, cy - crossSize), Offset(cx, cy + crossSize), strokeWidth)
    }
}
