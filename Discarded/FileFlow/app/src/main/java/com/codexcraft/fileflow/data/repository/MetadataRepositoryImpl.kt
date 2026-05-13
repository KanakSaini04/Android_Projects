package com.codexcraft.fileflow.data.repository

import android.net.Uri
import com.codexcraft.fileflow.core.db.dao.FavoriteDao
import com.codexcraft.fileflow.core.db.dao.RecentDao
import com.codexcraft.fileflow.core.db.dao.RecycleBinDao
import com.codexcraft.fileflow.core.db.entity.FavoriteEntity
import com.codexcraft.fileflow.core.db.entity.RecentEntity
import com.codexcraft.fileflow.core.db.entity.RecycleBinEntity
import com.codexcraft.fileflow.domain.repository.MetadataRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class MetadataRepositoryImpl @Inject constructor(
    private val recentDao: RecentDao,
    private val favoriteDao: FavoriteDao,
    private val recycleBinDao: RecycleBinDao
) : MetadataRepository {
    override suspend fun addRecent(uri: Uri, name: String, mimeType: String?) {
        recentDao.upsert(RecentEntity(uri.toString(), name, mimeType, System.currentTimeMillis()))
    }

    override fun getRecents(): Flow<List<RecentEntity>> = recentDao.getRecent()

    override suspend fun toggleFavorite(uri: Uri, name: String, mimeType: String?) {
        val key = uri.toString()
        val existing = favoriteDao.getByUri(key)
        if (existing == null) {
            favoriteDao.upsert(FavoriteEntity(key, name, mimeType, System.currentTimeMillis()))
        } else {
            favoriteDao.remove(key)
        }
    }

    override fun getFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.getFavorites()

    override suspend fun addToRecycleBin(
        originalUri: Uri,
        originalParentUri: Uri,
        name: String,
        mimeType: String?
    ) {
        recycleBinDao.insert(
            RecycleBinEntity(
                id = UUID.randomUUID().toString(),
                originalUriString = originalUri.toString(),
                originalParentUriString = originalParentUri.toString(),
                name = name,
                mimeType = mimeType,
                deletedAt = System.currentTimeMillis()
            )
        )
    }

    override fun getRecycleBin(): Flow<List<RecycleBinEntity>> = recycleBinDao.getAll()

    override suspend fun restoreFromRecycleBin(id: String): Boolean {
        val item = recycleBinDao.getById(id) ?: return false
        recycleBinDao.deleteById(item.id)
        return true
    }

    override suspend fun purgeFromRecycleBin(id: String) {
        recycleBinDao.deleteById(id)
    }
}
