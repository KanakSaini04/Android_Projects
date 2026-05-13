package com.codexcraft.fileflow.domain.usecase.browse

import com.codexcraft.fileflow.domain.repository.MetadataRepository
import javax.inject.Inject

class RestoreFromRecycleBinUseCase @Inject constructor(
    private val metadataRepository: MetadataRepository
) {
    suspend operator fun invoke(id: String): Boolean = metadataRepository.restoreFromRecycleBin(id)
}
