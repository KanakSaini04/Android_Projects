package com.codexcraft.fileflow.domain.usecase.tools

import android.net.Uri
import com.codexcraft.fileflow.domain.repository.ToolsRepository
import javax.inject.Inject

class ConvertImagesToPdfUseCase @Inject constructor(
    private val repository: ToolsRepository
) {
    suspend operator fun invoke(imageUris: List<Uri>, outputUri: Uri): Result<Unit> {
        return repository.convertImagesToPdf(imageUris, outputUri)
    }
}
