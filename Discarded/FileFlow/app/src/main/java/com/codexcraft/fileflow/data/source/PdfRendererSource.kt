package com.codexcraft.fileflow.data.source

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PdfRendererSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun open(uri: Uri): PdfRenderer {
        val pfd: ParcelFileDescriptor =
            context.contentResolver.openFileDescriptor(uri, "r")
                ?: throw IllegalStateException("Cannot open PDF")
        return PdfRenderer(pfd)
    }
}
