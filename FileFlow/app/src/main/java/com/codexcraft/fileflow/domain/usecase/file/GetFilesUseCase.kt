package com.codexcraft.fileflow.domain.usecase.file

import android.net.Uri
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.repository.FileRepository
import javax.inject.Inject

class GetFilesUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(directoryUri: Uri): List<FileItem> {
        return repository.getFiles(directoryUri)
    }
}
