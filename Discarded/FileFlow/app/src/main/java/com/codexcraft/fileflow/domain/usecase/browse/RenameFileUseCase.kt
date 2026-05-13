package com.codexcraft.fileflow.domain.usecase.browse

import android.net.Uri
import com.codexcraft.fileflow.data.source.SafStorageDataSource
import javax.inject.Inject

class RenameFileUseCase @Inject constructor(
    private val safStorageDataSource: SafStorageDataSource
) {
    suspend operator fun invoke(uri: Uri, newName: String): Boolean {
        if (newName.isBlank()) return false
        return safStorageDataSource.rename(uri, newName)
    }
}
