package com.lensora.data.repository

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.lensora.domain.model.CinematicFilter
import com.lensora.domain.repository.EditorRepository
import com.lensora.presentation.camera.components.mapFilterToGPUFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EditorRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : EditorRepository {

    override suspend fun aiEnhance(uri: Uri): Result<Uri> {
        return try {
            val bitmap = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            } ?: return Result.failure(Exception("Could not load image"))

            val gpuImage = GPUImage(context)
            gpuImage.setImage(bitmap)
            gpuImage.setFilter(
                GPUImageFilterGroup(
                    listOf(
                        GPUImageBrightnessFilter(0.05f),
                        GPUImageContrastFilter(1.15f),
                        GPUImageSharpenFilter(0.3f)
                    )
                )
            )
            val enhanced = gpuImage.bitmapWithFilterApplied
            Result.success(saveBitmap(enhanced))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun applyFilter(uri: Uri, filter: CinematicFilter): Result<Uri> {
        return try {
            val bitmap = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            } ?: return Result.failure(Exception("Could not load image"))

            val gpuImage = GPUImage(context)
            gpuImage.setImage(bitmap)
            gpuImage.setFilter(mapFilterToGPUFilter(filter))
            val filtered = gpuImage.bitmapWithFilterApplied
            Result.success(saveBitmap(filtered))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun savePhoto(uri: Uri): Result<Unit> = Result.success(Unit)

    override suspend fun sharePhoto(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share via").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun saveBitmap(bitmap: Bitmap): Uri {
        val filename = "LENSORA_EDIT_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        val file = File(context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES), filename)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }
}
