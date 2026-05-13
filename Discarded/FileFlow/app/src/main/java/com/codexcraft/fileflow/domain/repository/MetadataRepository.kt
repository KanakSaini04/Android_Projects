package com.codexcraft.fileflow.domain.repository

import android.net.Uri
import com.codexcraft.fileflow.core.db.entity.FavoriteEntity
import com.codexcraft.fileflow.core.db.entity.RecentEntity
import com.codexcraft.fileflow.core.db.entity.RecycleBinEntity
import kotlinx.coroutines.flow.Flow

interface MetadataRepository {
    suspend fun addRecent(uri: Uri, name: String, mimeType: String?)
    fun getRecents(): Flow<List<RecentEntity>>
    suspend fun toggleFavorite(uri: Uri, name: String, mimeType: String?)
    fun getFavorites(): Flow<List<FavoriteEntity>>
    suspend fun addToRecycleBin(originalUri: Uri, originalParentUri: Uri, name: String, mimeType: String?)
    fun getRecycleBin(): Flow<List<RecycleBinEntity>>
    suspend fun restoreFromRecycleBin(id: String): Boolean
    suspend fun purgeFromRecycleBin(id: String)
}
