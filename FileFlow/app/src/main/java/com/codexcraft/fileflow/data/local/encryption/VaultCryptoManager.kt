package com.codexcraft.fileflow.data.local.encryption

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.crypto.Cipher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultCryptoManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val keystoreManager: KeystoreManager
) {
    private val vaultDir = File(context.filesDir, "secure_vault").apply { mkdirs() }
    
    suspend fun encryptFile(
        sourceUri: Uri,
        fileName: String,
        cipher: Cipher
    ): Result<Pair<String, ByteArray>> = withContext(Dispatchers.IO) {
        try {
            val fileId = UUID.randomUUID().toString()
            val encryptedFile = File(vaultDir, "$fileId.enc")
            
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                encryptedFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        val encryptedBytes = cipher.update(buffer, 0, bytesRead)
                        if (encryptedBytes != null) {
                            output.write(encryptedBytes)
                        }
                    }
                    
                    val finalBytes = cipher.doFinal()
                    if (finalBytes != null) {
                        output.write(finalBytes)
                    }
                }
            } ?: return@withContext Result.failure(Exception("Cannot open source file"))
            
            Result.success(Pair(encryptedFile.absolutePath, cipher.iv))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun decryptFile(
        encryptedPath: String,
        iv: ByteArray
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val encryptedFile = File(encryptedPath)
            if (!encryptedFile.exists()) {
                return@withContext Result.failure(Exception("Encrypted file not found"))
            }
            
            val cipher = keystoreManager.getDecryptCipher(iv)
            
            encryptedFile.inputStream().use { input ->
                val encryptedData = input.readBytes()
                Result.success(cipher.doFinal(encryptedData))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteEncryptedFile(encryptedPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            File(encryptedPath).delete()
        } catch (e: Exception) {
            false
        }
    }
    
    fun getVaultSize(): Long {
        return vaultDir.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }
    
    suspend fun clearVault(): Boolean = withContext(Dispatchers.IO) {
        try {
            vaultDir.deleteRecursively()
            vaultDir.mkdirs()
            true
        } catch (e: Exception) {
            false
        }
    }
}
