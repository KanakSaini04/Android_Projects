package com.codexcraft.fileflow.data.repository

import android.net.Uri
import com.codexcraft.fileflow.data.local.db.dao.FavoriteFileDao
import com.codexcraft.fileflow.data.local.db.dao.RecentFileDao
import com.codexcraft.fileflow.data.local.db.entities.FavoriteFileEntity
import com.codexcraft.fileflow.data.local.db.entities.RecentFileEntity
import com.codexcraft.fileflow.data.local.storage.RecycleBinManager
import com.codexcraft.fileflow.data.local.storage.SafFileManager
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.model.StorageStats
import com.codexcraft.fileflow.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepositoryImpl @Inject constructor(
    private val safFileManager: SafFileManager,
    private val recycleBinManager: RecycleBinManager,
    private val favoriteFileDao: FavoriteFileDao,
    private val recentFileDao: RecentFileDao
) : FileRepository {
    
    override suspend fun getFiles(directoryUri: Uri): List<FileItem> {
        return safFileManager.getFiles(directoryUri)
    }
    
    override suspend fun searchFiles(directoryUri: Uri, query: String): List<FileItem> {
        return safFileManager.searchFiles(directoryUri, query)
    }
    
    override suspend fun deleteFile(uri: Uri): Boolean {
        return safFileManager.deleteFile(uri)
    }
    
    override suspend fun renameFile(uri: Uri, newName: String): Boolean {
        return safFileManager.renameFile(uri, newName)
    }
    
    override suspend fun moveFile(sourceUri: Uri, targetDirUri: Uri): Boolean {
        return safFileManager.moveFile(sourceUri, targetDirUri)
    }
    
    override suspend fun copyFile(sourceUri: Uri, targetDirUri: Uri): Boolean {
        return safFileManager.copyFile(sourceUri, targetDirUri)
    }
    
    override suspend fun createDirectory(parentUri: Uri, dirName: String): Uri? {
        return safFileManager.createDirectory(parentUri, dirName)
    }
    
    override suspend fun getStorageStats(): StorageStats {
        return safFileManager.getStorageStats()
    }
    
    override fun getFavorites(): Flow<List<FileItem>> {
        return favoriteFileDao.getAllFavorites().map { entities ->
            entities.map { entity ->
                FileItem(
                    uri = Uri.parse(entity.uri),
                    name = entity.name,
                    size = 0L,
                    mimeType = entity.mimeType,
                    lastModified = entity.addedAt,
                    isDirectory = false,
                    path = entity.uri
                )
            }
        }
    }
    
    override suspend fun addFavorite(fileItem: FileItem) {
        favoriteFileDao.addFavorite(
            FavoriteFileEntity(
                uri = fileItem.uri.toString(),
                name = fileItem.name,
                mimeType = fileItem.mimeType
            )
        )
    }
    
    override suspend fun removeFavorite(uri: String) {
        favoriteFileDao.removeFavoriteByUri(uri)
    }
    
    override suspend fun isFavorite(uri: String): Boolean {
        return favoriteFileDao.isFavorite(uri)
    }
    
    override fun getRecentFiles(): Flow<List<FileItem>> {
        return recentFileDao.getRecentFiles().map { entities ->
            entities.map { entity ->
                FileItem(
                    uri = Uri.parse(entity.uri),
                    name = entity.name,
                    size = 0L,
                    mimeType = entity.mimeType,
                    lastModified = entity.accessedAt,
                    isDirectory = false,
                    path = entity.uri
                )
            }
        }
    }
    
    override suspend fun addRecentFile(fileItem: FileItem) {
        recentFileDao.addRecentFile(
            RecentFileEntity(
                uri = fileItem.uri.toString(),
                name = fileItem.name,
                mimeType = fileItem.mimeType
            )
        )
    }
    
    override suspend fun clearRecentFiles() {
        recentFileDao.clearAllRecents()
    }
    
    override suspend fun moveToRecycleBin(fileUri: Uri, originalPath: String): Boolean {
        return recycleBinManager.moveToRecycleBin(fileUri, originalPath)
    }
    
    override suspend fun getRecycleBinFiles(): List<FileItem> {
        return recycleBinManager.getRecycleBinFiles()
    }
    
    override suspend fun restoreFromRecycleBin(fileUri: Uri): Boolean {
        return recycleBinManager.restoreFromRecycleBin(fileUri)
    }
    
    override suspend fun emptyRecycleBin(): Boolean {
        return recycleBinManager.emptyRecycleBin()
    }
}
