package com.codexcraft.fileflow.domain.usecase.browse

import android.net.Uri
import com.codexcraft.fileflow.data.source.SafStorageDataSource
import javax.inject.Inject

class MoveFileUseCase @Inject constructor(
    private val safStorageDataSource: SafStorageDataSource
) {
    suspend operator fun invoke(sourceUri: Uri, targetParentUri: Uri): Boolean {
        return safStorageDataSource.move(sourceUri, targetParentUri)
    }
}
