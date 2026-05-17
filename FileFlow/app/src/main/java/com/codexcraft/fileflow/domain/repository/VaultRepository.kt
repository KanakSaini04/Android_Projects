package com.codexcraft.fileflow.domain.repository

import android.net.Uri
import com.codexcraft.fileflow.domain.model.VaultFile

interface VaultRepository {
    suspend fun getVaultFiles(): List<VaultFile>
    suspend fun encryptAndAddFile(uri: Uri): VaultFile
    suspend fun decryptFile(vaultFile: VaultFile): ByteArray
    suspend fun deleteVaultFile(vaultFile: VaultFile)
    suspend fun clearVault()
}
