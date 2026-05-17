package com.codexcraft.fileflow.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserSyncData(
    val userId: String,
    val favorites: List<FavoriteDto>,
    val lastSyncTimestamp: Long
)

@Serializable
data class FavoriteDto(
    val uri: String,
    val name: String,
    val mimeType: String,
    val addedAt: Long
)
