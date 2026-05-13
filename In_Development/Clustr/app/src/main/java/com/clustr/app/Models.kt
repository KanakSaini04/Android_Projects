package com.clustr.app

import androidx.compose.ui.graphics.Color
import com.google.firebase.Timestamp
import kotlin.math.cos
import kotlin.math.sin

// ─── User & Authentication Models ─────────────────────────────────────────────

data class UserProfile(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val biometricEnabled: Boolean = false,
    val micEnabled: Boolean = true,
    val createdAt: Timestamp? = null
)

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: UserProfile? = null
)

// ─── Recording Data Models ────────────────────────────────────────────────────

data class NodeSnapshot(
    val x: Float,
    val y: Float,
    val z: Float,
    val frequencyHz: Float,
    val radius: Float
)

data class VoiceRecord(
    val id: String = "",
    val uid: String,
    val title: String,
    val durationMs: Long,
    val createdAt: Timestamp? = null,
    val nodeSnapshots: List<NodeSnapshot> = emptyList(), // Standardized plural naming
    val peakFrequencyHz: Float = 0f,
    val peakAmplitude: Float = 0f
)

/**
 * Helper to convert a saved snapshot back into a live 3D node for the detail view.
 */
fun NodeSnapshot.toVoiceNode(): VoiceNode {
    return VoiceNode(
        x3d = this.x,
        y3d = this.y,
        z3d = this.z,
        radius = this.radius,
        color = frequencyToColor(this.frequencyHz),
        frequencyHz = this.frequencyHz
    )
}

// ─── Visualizer Data Models ───────────────────────────────────────────────────

data class AudioFrame(
    val frequency: Float,
    val amplitude: Float,
    val rawRms: Float
)

data class VoiceNode(
    val x3d: Float,
    val y3d: Float,
    val z3d: Float,
    val radius: Float,
    val color: Color,
    val frequencyHz: Float,
    var life: Float = 1f,
    val decay: Float = 0.006f,
    var labelCountdown: Int = 0
)

data class Projected(
    val x: Float,
    val y: Float,
    val scale: Float,
    val node: VoiceNode
)

// ─── Projection Engine ────────────────────────────────────────────────────────

object Projection {
    private const val FOCAL_LENGTH = 800f
    private const val Z_OFFSET = 100f

    fun project(
        node: VoiceNode,
        rotationY: Float,
        screenCx: Float,
        screenCy: Float
    ): Projected {
        val cosR = cos(rotationY)
        val sinR = sin(rotationY)
        val rx = node.x3d * cosR - node.z3d * sinR
        val rz = node.x3d * sinR + node.z3d * cosR

        val scale = FOCAL_LENGTH / (FOCAL_LENGTH + rz + Z_OFFSET)
        val sx = screenCx + rx * scale
        val sy = screenCy + node.y3d * scale

        return Projected(sx, sy, scale, node)
    }
}

// ─── Helper Functions ─────────────────────────────────────────────────────────

fun frequencyToColor(hz: Float): Color {
    val t = ((hz - 80f) / (8000f - 80f)).coerceIn(0f, 1f)
    val hue = (260f - t * 260f).coerceIn(0f, 360f)
    return Color.hsl(hue, saturation = 0.95f, lightness = 0.52f)
}

fun formatHz(hz: Float): String {
    return if (hz >= 1000f) "${"%.1f".format(hz / 1000f)}k"
    else "${hz.toInt()}"
}