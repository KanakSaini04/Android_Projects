package com.codexcraft.fileflow.core.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile

object UriUtils {
    fun getFileName(context: Context, uri: Uri): String {
        var name = "file"
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && cursor.moveToFirst()) name = cursor.getString(idx)
        }
        return name
    }

    fun getMimeType(context: Context, uri: Uri): String? = context.contentResolver.getType(uri)

    fun getDocumentFile(context: Context, uri: Uri): DocumentFile? =
        DocumentFile.fromSingleUri(context, uri) ?: DocumentFile.fromTreeUri(context, uri)
}
