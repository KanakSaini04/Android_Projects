package com.lensora.domain.usecase.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageProxy
import com.lensora.domain.model.PoseResult
import com.lensora.domain.repository.CameraRepository
import javax.inject.Inject

class DetectSceneUseCase @Inject constructor(private val repo: CameraRepository) {
    suspend operator fun invoke(imageProxy: ImageProxy): String? = repo.detectScene(imageProxy)
}
class CapturePhotoUseCase @Inject constructor(private val repo: CameraRepository) {
    suspend operator fun invoke(context: Context): Result<Uri> = repo.capturePhoto(context)
}
class AnalyzePoseUseCase @Inject constructor(private val repo: CameraRepository) {
    suspend operator fun invoke(imageProxy: ImageProxy): PoseResult? = repo.analyzePose(imageProxy)
}
