package com.lensora.presentation.camera.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.lensora.domain.model.PoseResult

@Composable
fun PoseOverlay(modifier: Modifier = Modifier, poseResult: PoseResult?) {
    Canvas(modifier = modifier) {
        poseResult?.landmarks?.let { landmarks ->
            if (landmarks.size < 17) return@Canvas
            val connections = listOf(
                Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4),
                Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8),
                Pair(9, 10), Pair(11, 12), Pair(11, 13), Pair(13, 15),
                Pair(12, 14), Pair(14, 16)
            )
            connections.forEach { (start, end) ->
                if (start < landmarks.size && end < landmarks.size) {
                    val s = landmarks[start]
                    val e = landmarks[end]
                    if (s.visibility > 0.5f && e.visibility > 0.5f) {
                        drawLine(
                            color = Color(0xFF4FC3F7).copy(alpha = 0.7f),
                            start = Offset(s.x * size.width, s.y * size.height),
                            end = Offset(e.x * size.width, e.y * size.height),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
            landmarks.forEach { lm ->
                if (lm.visibility > 0.5f) {
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = Offset(lm.x * size.width, lm.y * size.height)
                    )
                    drawCircle(
                        color = Color(0xFF4FC3F7),
                        radius = 4f,
                        center = Offset(lm.x * size.width, lm.y * size.height)
                    )
                }
            }
        }
    }
}
