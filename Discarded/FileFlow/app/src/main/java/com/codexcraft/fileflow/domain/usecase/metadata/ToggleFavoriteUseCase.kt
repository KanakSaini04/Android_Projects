package com.codexcraft.fileflow.domain.usecase.metadata

import android.net.Uri
import com.codexcraft.fileflow.domain.repository.MetadataRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val metadataRepository: MetadataRepository
) {
    suspend operator fun invoke(uri: Uri, name: String, mimeType: String?) {
        metadataRepository.toggleFavorite(uri, name, mimeType)
    }
}
