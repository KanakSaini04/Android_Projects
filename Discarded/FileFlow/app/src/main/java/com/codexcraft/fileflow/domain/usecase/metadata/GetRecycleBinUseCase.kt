package com.codexcraft.fileflow.domain.usecase.metadata

import com.codexcraft.fileflow.domain.repository.MetadataRepository
import javax.inject.Inject

class GetRecycleBinUseCase @Inject constructor(
    private val metadataRepository: MetadataRepository
) {
    operator fun invoke() = metadataRepository.getRecycleBin()
}
