package com.codexcraft.fileflow.domain.usecase.file

import android.net.Uri
import com.codexcraft.fileflow.domain.repository.FileRepository
import javax.inject.Inject

class DeleteFileUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(uri: Uri): Boolean {
        return repository.deleteFile(uri)
    }
}
