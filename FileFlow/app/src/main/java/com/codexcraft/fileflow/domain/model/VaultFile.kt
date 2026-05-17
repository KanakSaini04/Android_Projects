package com.codexcraft.fileflow.domain.model

data class VaultFile(
    val id: String,
    val originalName: String,
    val encryptedPath: String,
    val size: Long,
    val mimeType: String,
    val encryptedAt: Long,
    val iv: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VaultFile
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
