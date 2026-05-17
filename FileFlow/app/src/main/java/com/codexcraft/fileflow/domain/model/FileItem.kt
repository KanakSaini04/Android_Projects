package com.codexcraft.fileflow.domain.model

import android.net.Uri

data class FileItem(
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String,
    val lastModified: Long,
    val isDirectory: Boolean,
    val path: String,
    val extension: String = name.substringAfterLast('.', ""),
    val category: FileCategory = FileCategory.fromMimeType(mimeType)
)
