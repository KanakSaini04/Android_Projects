package com.codexcraft.fileflow.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault")
data class VaultEntity(
    @PrimaryKey val id: String,
    val originalName: String,
    val mimeType: String?,
    val internalFileName: String,
    val encryptedAt: Long
)
