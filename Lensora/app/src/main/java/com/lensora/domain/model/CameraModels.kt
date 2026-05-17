package com.lensora.domain.model

enum class CameraMode {
    AUTO, PORTRAIT, NIGHT, TRAVEL, VIDEO
}

enum class CinematicFilter {
    CINEMATIC,
    GOLDEN_HOUR,
    TOKYO_NIGHT,
    SOFT_PORTRAIT,
    MOUNTAIN_AIR,
    MOODY_STREET,
    TROPICAL_WARM,
    VINTAGE_FILM
}

data class PoseResult(
    val landmarks: List<PoseLandmark>,
    val isAligned: Boolean = false
)

data class PoseLandmark(
    val x: Float,
    val y: Float,
    val z: Float,
    val visibility: Float
)
