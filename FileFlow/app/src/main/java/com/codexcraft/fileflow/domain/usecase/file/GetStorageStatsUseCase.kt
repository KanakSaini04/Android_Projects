package com.codexcraft.fileflow.domain.usecase.file

import com.codexcraft.fileflow.domain.model.StorageStats
import com.codexcraft.fileflow.domain.repository.FileRepository
import javax.inject.Inject

class GetStorageStatsUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(): StorageStats {
        return repository.getStorageStats()
    }
}
