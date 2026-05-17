package com.lensora.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
    val uri: String,
    val timestamp: Long,
    val scene: String? = null,
    val filter: String? = null,
    val isBestShot: Boolean = false,
    val tags: String = ""
)
