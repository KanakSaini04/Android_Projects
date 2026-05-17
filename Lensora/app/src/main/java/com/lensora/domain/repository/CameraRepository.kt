package com.lensora.domain.repository

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageProxy
import com.lensora.domain.model.PoseResult

interface CameraRepository {
    suspend fun detectScene(imageProxy: ImageProxy): String?
    suspend fun capturePhoto(context: Context): Result<Uri>
    suspend fun analyzePose(imageProxy: ImageProxy): PoseResult?
}
