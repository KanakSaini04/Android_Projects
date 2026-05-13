package com.codexcraft.caretap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    // Compress and save bitmap to app's internal storage
    fun compressAndSave(context: Context, uri: Uri, profileId: String): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Resize to max 400x400
            val scaled = Bitmap.createScaledBitmap(original, 400, 400, true)

            // Save to internal storage
            val file = File(context.filesDir, "profile_$profileId.jpg")
            val outputStream = FileOutputStream(file)
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}