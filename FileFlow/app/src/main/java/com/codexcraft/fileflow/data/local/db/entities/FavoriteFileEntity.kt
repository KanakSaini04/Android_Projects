package com.codexcraft.fileflow.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_files")
data class FavoriteFileEntity(
    @PrimaryKey val uri: String,
    val name: String,
    val mimeType: String,
    val addedAt: Long = System.currentTimeMillis()
)
