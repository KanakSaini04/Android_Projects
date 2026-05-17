package com.codexcraft.fileflow.domain.usecase.vault

import android.net.Uri
import com.codexcraft.fileflow.domain.repository.VaultRepository
import javax.crypto.Cipher
import javax.inject.Inject

class EncryptFileUseCase @Inject constructor(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(sourceUri: Uri, fileName: String, cipher: Cipher): Result<Unit> {
        return repository.encryptFile(sourceUri, fileName, cipher)
    }
}
