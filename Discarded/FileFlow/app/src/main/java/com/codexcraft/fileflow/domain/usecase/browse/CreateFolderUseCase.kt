package com.codexcraft.fileflow.domain.usecase.browse

import android.net.Uri
import com.codexcraft.fileflow.data.source.SafStorageDataSource
import javax.inject.Inject

class CreateFolderUseCase @Inject constructor(
    private val safStorageDataSource: SafStorageDataSource
) {
    suspend operator fun invoke(parentUri: Uri, name: String): Uri? {
        if (name.isBlank()) return null
        return safStorageDataSource.createDirectory(parentUri, name)
    }
}
