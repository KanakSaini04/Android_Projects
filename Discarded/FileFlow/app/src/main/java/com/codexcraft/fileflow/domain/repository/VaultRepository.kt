package com.codexcraft.fileflow.domain.repository

import android.net.Uri

data class VaultItem(
    val id: String,
    val name: String,
    val encryptedUri: Uri,
    val mimeType: String?
)

interface VaultRepository {
    suspend fun encryptToVault(uri: Uri, name: String): VaultItem
    suspend fun decryptFromVault(item: VaultItem, targetUri: Uri): Boolean
    suspend fun listVault(): List<VaultItem>
}
