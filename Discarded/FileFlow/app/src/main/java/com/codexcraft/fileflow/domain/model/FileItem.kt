package com.codexcraft.fileflow.domain.model

import android.net.Uri

data class FileItem(
    val uri: Uri,
    val name: String,
    val mimeType: String?,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val lastModified: Long
)
