package com.codexcraft.fileflow.domain.usecase.browse

import android.net.Uri
import com.codexcraft.fileflow.data.source.SafStorageDataSource
import com.codexcraft.fileflow.domain.repository.MetadataRepository
import javax.inject.Inject

class DeleteFileUseCase @Inject constructor(
    private val safStorageDataSource: SafStorageDataSource,
    private val metadataRepository: MetadataRepository
) {
    suspend operator fun invoke(uri: Uri, parentUri: Uri, name: String, mimeType: String?) {
        metadataRepository.addToRecycleBin(uri, parentUri, name, mimeType)
        safStorageDataSource.delete(uri)
    }
}
