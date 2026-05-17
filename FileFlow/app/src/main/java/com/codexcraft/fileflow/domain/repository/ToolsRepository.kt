package com.codexcraft.fileflow.domain.repository

import android.net.Uri
import com.codexcraft.fileflow.domain.model.FileItem
import kotlinx.coroutines.flow.Flow

interface ToolsRepository {
    suspend fun convertImagesToPdf(imageUris: List<Uri>, outputUri: Uri): Result<Unit>
    suspend fun findDuplicateFiles(directoryUri: Uri): Flow<List<List<FileItem>>>
    suspend fun startFlowShareServer(): Result<String>
    suspend fun stopFlowShareServer()
}
