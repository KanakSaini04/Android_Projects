package com.codexcraft.fileflow.domain.usecase.tools

import android.net.Uri
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.repository.ToolsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FindDuplicatesUseCase @Inject constructor(
    private val repository: ToolsRepository
) {
    suspend operator fun invoke(directoryUri: Uri): Flow<List<List<FileItem>>> {
        return repository.findDuplicateFiles(directoryUri)
    }
}
