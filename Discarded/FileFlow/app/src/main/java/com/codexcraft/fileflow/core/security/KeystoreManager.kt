package com.codexcraft.fileflow.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

interface KeystoreManager {
    fun getOrCreateSecretKey(alias: String): SecretKey
}

@Singleton
class KeystoreManagerImpl @Inject constructor() : KeystoreManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    override fun getOrCreateSecretKey(alias: String): SecretKey {
        return (keyStore.getKey(alias, null) as? SecretKey) ?: generateSecretKey(alias)
    }

    private fun generateSecretKey(alias: String): SecretKey {
        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
}
