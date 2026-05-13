package com.clustr.app.ui.screens.visualizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import com.clustr.app.Projected
import com.clustr.app.Projection
import com.clustr.app.VoiceNode
import com.clustr.app.formatHz
import kotlin.math.sqrt

@Composable
fun ClustrCanvas(
    nodes: List<VoiceNode>,
    rotationY: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        val projected = nodes
            .map { Projection.project(it, rotationY, cx, cy) }
            .sortedBy { it.scale }

        drawFilaments(projected)
        drawSpheres(projected)
        drawLabels(projected)
    }
}

private fun DrawScope.drawFilaments(projected: List<Projected>) {
    val max = minOf(projected.size, 300)
    for (i in 1 until max) {
        val a = projected[i - 1]; val b = projected[i]
        val dx = a.x - b.x; val dy = a.y - b.y
        if (sqrt(dx * dx + dy * dy) > 280f) continue
        val alpha = ((a.node.life + b.node.life) / 2f * 0.2f).coerceIn(0f, 0.22f)
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(a.x, a.y),
            end   = Offset(b.x, b.y),
            strokeWidth = 0.55f,
            cap = StrokeCap.Round
        )
    }
    // Sparse long-range web connections
    val step = (projected.size / 18).coerceAtLeast(4)
    for (i in 0 until projected.size - step step step) {
        val a = projected[i]; val b = projected[i + step]
        val alpha = ((a.node.life + b.node.life) / 2f * 0.06f).coerceIn(0f, 0.09f)
        drawLine(
            color = Color.White.copy(alpha = alpha),
            start = Offset(a.x, a.y),
            end   = Offset(b.x, b.y),
            strokeWidth = 0.35f
        )
    }
}

private fun DrawScope.drawSpheres(projected: List<Projected>) {
    for (p in projected) {
        val r     = (p.node.radius * p.scale).coerceIn(2f, 64f)
        val alpha = p.node.life.coerceIn(0f, 1f)

        // Main solid sphere
        drawCircle(
            color  = p.node.color.copy(alpha = alpha),
            radius = r,
            center = Offset(p.x, p.y)
        )
        // Subtle specular highlight — makes it feel like a 3D ball
        drawCircle(
            color  = Color.White.copy(alpha = alpha * 0.22f),
            radius = r * 0.32f,
            center = Offset(p.x - r * 0.18f, p.y - r * 0.18f)
        )
    }
}

private fun DrawScope.drawLabels(projected: List<Projected>) {
    drawIntoCanvas { canvas ->
        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            typeface    = android.graphics.Typeface.MONOSPACE
        }
        for (p in projected) {
            if (p.node.labelCountdown <= 0) continue
            val r     = (p.node.radius * p.scale).coerceIn(2f, 64f)
            val fade  = (p.node.labelCountdown / 90f).coerceIn(0f, 1f)
            val alpha = (p.node.life * fade * 0.85f).coerceIn(0f, 1f)
            paint.textSize = (8f + r * 0.28f).coerceIn(8f, 13f)
            paint.color = android.graphics.Color.argb(
                (alpha * 210).toInt().coerceIn(0, 255), 200, 200, 200
            )
            canvas.nativeCanvas.drawText(
                formatHz(p.node.frequencyHz),
                p.x + r + 5f, p.y + paint.textSize / 3f, paint
            )
        }
    }
}