package com.codexcraft.fileflow.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recycle_bin")
data class RecycleBinEntity(
    @PrimaryKey val id: String,
    val originalUriString: String,
    val originalParentUriString: String,
    val name: String,
    val mimeType: String?,
    val deletedAt: Long
)
