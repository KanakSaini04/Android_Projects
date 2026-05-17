package com.codexcraft.fileflow.core.security

import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

interface CryptoManager {
    fun encryptStream(input: InputStream, output: OutputStream, alias: String)
    fun decryptStream(input: InputStream, output: OutputStream, alias: String)
}

@Singleton
class CryptoManagerImpl @Inject constructor(
    private val keystoreManager: KeystoreManager
) : CryptoManager {

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
        private const val TAG_SIZE = 128
        private const val BUFFER_SIZE = 64 * 1024
    }

    override fun encryptStream(input: InputStream, output: OutputStream, alias: String) {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val key = keystoreManager.getOrCreateSecretKey(alias)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        // Write IV first
        output.write(cipher.iv)

        CipherOutputStream(output, cipher).use { cipherOut ->
            input.copyTo(cipherOut, BUFFER_SIZE)
        }
    }

    override fun decryptStream(input: InputStream, output: OutputStream, alias: String) {
        val iv = ByteArray(IV_SIZE)
        if (input.read(iv) != IV_SIZE) throw IllegalStateException("Invalid encrypted file")

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val key = keystoreManager.getOrCreateSecretKey(alias)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))

        CipherInputStream(input, cipher).use { cipherIn ->
            cipherIn.copyTo(output, BUFFER_SIZE)
        }
    }
}