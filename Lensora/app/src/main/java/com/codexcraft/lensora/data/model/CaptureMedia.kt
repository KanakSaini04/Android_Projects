package com.codexcraft.lensora.data.model

import java.io.File

data class CapturedMedia(
    val id: String,
    val file: File,
    val timestamp: Long,
    val type: MediaType,
    val aiMode: String = "Auto"
)

enum class MediaType { PHOTO, VIDEO }