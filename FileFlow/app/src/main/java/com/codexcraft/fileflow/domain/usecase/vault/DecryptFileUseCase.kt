package com.codexcraft.fileflow.domain.usecase.vault

import com.codexcraft.fileflow.domain.model.VaultFile
import com.codexcraft.fileflow.domain.repository.VaultRepository
import javax.inject.Inject

class DecryptFileUseCase @Inject constructor(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(file: VaultFile): Result<ByteArray> {
        return repository.decryptFile(file)
    }
}
