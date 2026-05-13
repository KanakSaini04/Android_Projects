package com.codexcraft.fileflow.domain.repository

import android.net.Uri
import com.codexcraft.fileflow.domain.model.FileItem

interface FileRepository {
    suspend fun getFiles(parentUri: Uri): Result<List<FileItem>>
}
