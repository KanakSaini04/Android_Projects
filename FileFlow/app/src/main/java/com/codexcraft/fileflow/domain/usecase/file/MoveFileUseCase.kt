package com.codexcraft.fileflow.domain.usecase.file

import android.net.Uri
import com.codexcraft.fileflow.domain.repository.FileRepository
import javax.inject.Inject

class MoveFileUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(sourceUri: Uri, targetDirUri: Uri): Boolean {
        return repository.moveFile(sourceUri, targetDirUri)
    }
}
