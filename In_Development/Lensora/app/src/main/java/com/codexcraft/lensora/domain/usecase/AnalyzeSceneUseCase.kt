package com.codexcraft.lensora.domain.usecase

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

enum class AiCameraMode(val label: String, val emoji: String) {
    PORTRAIT("Portrait", "👤"),
    NIGHT("Night", "🌙"),
    MACRO("Macro", "🔬"),
    AUTO("Auto", "✨"),
    LANDSCAPE("Landscape", "🏔")
}

class AnalyzeSceneUseCase @Inject constructor() {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.65f)
            .build()
    )

    suspend operator fun invoke(bitmap: Bitmap): AiCameraMode =
        suspendCancellableCoroutine { cont ->
            val image = InputImage.fromBitmap(bitmap, 0)
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    val labelNames = labels.map { it.text.lowercase() }
                    val mode = when {
                        labelNames.any { it.contains("person") || it.contains("face") || it.contains("portrait") } ->
                            AiCameraMode.PORTRAIT
                        labelNames.any { it.contains("night") || it.contains("dark") || it.contains("sky") } ->
                            AiCameraMode.NIGHT
                        labelNames.any { it.contains("flower") || it.contains("insect") || it.contains("macro") || it.contains("leaf") } ->
                            AiCameraMode.MACRO
                        labelNames.any { it.contains("mountain") || it.contains("landscape") || it.contains("nature") } ->
                            AiCameraMode.LANDSCAPE
                        else -> AiCameraMode.AUTO
                    }
                    if (cont.isActive) cont.resume(mode)
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(AiCameraMode.AUTO)
                }
        }
}