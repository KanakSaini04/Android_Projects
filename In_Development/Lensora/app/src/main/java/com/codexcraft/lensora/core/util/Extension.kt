package com.codexcraft.lensora.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

fun ImageProxy.toBitmap(): Bitmap? {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return try {
        val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix().apply { postRotate(imageInfo.rotationDegrees.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } catch (e: Exception) {
        null
    }
}

fun Context.createMediaFile(extension: String): File {
    val mediaDir = getExternalFilesDir("Lensora")
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    return File(mediaDir, "LENSORA_${timestamp}.$extension")
}