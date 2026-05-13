package com.codexcraft.fileflow.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recents")
data class RecentEntity(
    @PrimaryKey val uriString: String,
    val name: String,
    val mimeType: String?,
    val lastOpenedAt: Long
)
