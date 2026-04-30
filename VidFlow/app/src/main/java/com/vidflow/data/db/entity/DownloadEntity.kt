package com.vidflow.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val thumbnail: String,
    val quality: String,
    val downloadUrl: String,
    val filePath: String? = null,
    val status: String = "queued",  // queued | downloading | completed | failed
    val progress: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)