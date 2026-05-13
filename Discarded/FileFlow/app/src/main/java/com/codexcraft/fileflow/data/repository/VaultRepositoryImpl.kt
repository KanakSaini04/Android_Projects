package com.codexcraft.fileflow.data.repository

import android.content.Context
import android.net.Uri
import com.codexcraft.fileflow.core.db.dao.VaultDao
import com.codexcraft.fileflow.core.db.entity.VaultEntity
import com.codexcraft.fileflow.core.security.CryptoManager
import com.codexcraft.fileflow.domain.repository.VaultItem
import com.codexcraft.fileflow.domain.repository.VaultRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager,
    private val vaultDao: VaultDao
) : VaultRepository {

    private val vaultDir = File(context.filesDir, "secure_vault").apply { mkdirs() }

    override suspend fun encryptToVault(uri: Uri, name: String): VaultItem = withContext(Dispatchers.IO) {
        val mimeType = context.contentResolver.getType(uri)
        val internalFileName = UUID.randomUUID().toString()
        val outFile = File(vaultDir, internalFileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            outFile.outputStream().use { output ->
                cryptoManager.encryptStream(input, output, "VaultKey")
            }
        } ?: throw IllegalStateException("Cannot read file")

        val entity = VaultEntity(
            id = internalFileName,
            originalName = name,
            mimeType = mimeType,
            internalFileName = internalFileName,
            encryptedAt = System.currentTimeMillis()
        )
        vaultDao.insert(entity)

        VaultItem(entity.id, name, Uri.fromFile(outFile), mimeType)
    }

    override suspend fun decryptFromVault(item: VaultItem, targetUri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val inFile = File(vaultDir, item.id)
            if (!inFile.exists()) return@runCatching false

            inFile.inputStream().use { input ->
                context.contentResolver.openOutputStream(targetUri, "wt")?.use { output ->
                    cryptoManager.decryptStream(input, output, "VaultKey")
                } ?: return@runCatching false
            }

            inFile.delete()
            vaultDao.deleteById(item.id)
            true
        }.getOrDefault(false)
    }

    override suspend fun listVault(): List<VaultItem> = withContext(Dispatchers.IO) {
        vaultDao.getAll().first().map { entity ->
            VaultItem(
                id = entity.id,
                name = entity.originalName,
                encryptedUri = Uri.fromFile(File(vaultDir, entity.internalFileName)),
                mimeType = entity.mimeType
            )
        }
    }
}
