package com.codexcraft.fileflow.domain.usecase.browse

import android.net.Uri
import com.codexcraft.fileflow.domain.repository.FileRepository
import javax.inject.Inject

class ListFilesUseCase @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend operator fun invoke(parentUri: Uri) = fileRepository.getFiles(parentUri)
}
