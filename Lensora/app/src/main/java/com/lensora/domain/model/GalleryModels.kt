package com.lensora.domain.model

import android.net.Uri

data class GalleryPhoto(
    val id: String,
    val uri: Uri,
    val timestamp: Long,
    val isBestShot: Boolean = false,
    val tags: List<String> = emptyList(),
    val scene: String? = null
)
