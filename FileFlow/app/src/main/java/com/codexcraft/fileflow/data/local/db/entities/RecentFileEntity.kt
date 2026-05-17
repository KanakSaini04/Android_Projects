package com.codexcraft.fileflow.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey val uri: String,
    val name: String,
    val mimeType: String,
    val accessedAt: Long = System.currentTimeMillis()
)
