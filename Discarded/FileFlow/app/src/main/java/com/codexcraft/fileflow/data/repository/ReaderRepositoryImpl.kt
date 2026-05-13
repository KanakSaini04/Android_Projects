package com.codexcraft.fileflow.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import com.codexcraft.fileflow.data.source.PdfRendererSource
import com.codexcraft.fileflow.domain.repository.ReaderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject

class ReaderRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfRendererSource: PdfRendererSource
) : ReaderRepository {
    override suspend fun readText(uri: Uri): String = withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { input ->
            BufferedReader(InputStreamReader(input)).readText()
        } ?: ""
    }

    override suspend fun writeText(uri: Uri, text: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                BufferedWriter(OutputStreamWriter(output)).apply {
                    write(text)
                    flush()
                }
            } != null
        }.getOrDefault(false)
    }

    override suspend fun getPdfPageCount(uri: Uri): Int = withContext(Dispatchers.IO) {
        pdfRendererSource.open(uri).use { renderer ->
            renderer.pageCount
        }
    }

    override suspend fun renderPdfPage(uri: Uri, pageIndex: Int, width: Int, height: Int): Bitmap =
        withContext(Dispatchers.IO) {
            pdfRendererSource.open(uri).use { renderer ->
                val page = renderer.openPage(pageIndex)
                val bmp = Bitmap.createBitmap(
                    width.coerceAtLeast(1),
                    height.coerceAtLeast(1),
                    Bitmap.Config.ARGB_8888
                )
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                bmp
            }
        }
}
