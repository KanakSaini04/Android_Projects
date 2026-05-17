package com.lensora.data.repository

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.lensora.domain.model.PoseResult
import com.lensora.domain.repository.CameraRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume

class CameraRepositoryImpl @Inject constructor() : CameraRepository {

    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.Builder().setConfidenceThreshold(0.6f).build()
    )

    override suspend fun detectScene(imageProxy: ImageProxy): String? {
        return suspendCancellableCoroutine { cont ->
            try {
                val image = InputImage.fromMediaImage(
                    imageProxy.image ?: return@suspendCancellableCoroutine,
                    imageProxy.imageInfo.rotationDegrees
                )
                labeler.process(image)
                    .addOnSuccessListener { labels -> cont.resume(labels.firstOrNull()?.text) }
                    .addOnFailureListener { cont.resume(null) }
            } catch (e: Exception) {
                cont.resume(null)
            }
        }
    }

    override suspend fun capturePhoto(context: Context): Result<Uri> {
        return try {
            val outputFile = File(
                context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES),
                "LENSORA_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
            )
            Result.success(Uri.fromFile(outputFile))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun analyzePose(imageProxy: ImageProxy): PoseResult? = null
}
