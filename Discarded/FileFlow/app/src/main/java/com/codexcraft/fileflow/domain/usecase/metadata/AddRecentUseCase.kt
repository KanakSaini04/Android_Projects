package com.codexcraft.fileflow.domain.usecase.metadata

import android.net.Uri
import com.codexcraft.fileflow.domain.repository.MetadataRepository
import javax.inject.Inject

class AddRecentUseCase @Inject constructor(
    private val repository: MetadataRepository
) {
    suspend operator fun invoke(uri: Uri, name: String, mimeType: String?) {
        repository.addRecent(uri, name, mimeType)
    }
}
