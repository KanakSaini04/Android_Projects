package com.codexcraft.fileflow.domain.repository

import android.graphics.Bitmap
import android.net.Uri

interface ReaderRepository {
    suspend fun readText(uri: Uri): String
    suspend fun writeText(uri: Uri, text: String): Boolean
    suspend fun getPdfPageCount(uri: Uri): Int
    suspend fun renderPdfPage(uri: Uri, pageIndex: Int, width: Int, height: Int): Bitmap
}
