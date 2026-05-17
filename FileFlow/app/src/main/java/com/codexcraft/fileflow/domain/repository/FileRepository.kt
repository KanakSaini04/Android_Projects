package com.codexcraft.fileflow.domain.repository

import android.net.Uri
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.model.StorageStats
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    suspend fun getFiles(directoryUri: Uri): List<FileItem>
    suspend fun searchFiles(directoryUri: Uri, query: String): List<FileItem>
    suspend fun deleteFile(uri: Uri): Boolean
    suspend fun renameFile(uri: Uri, newName: String): Boolean
    suspend fun moveFile(sourceUri: Uri, targetDirUri: Uri): Boolean
    suspend fun copyFile(sourceUri: Uri, targetDirUri: Uri): Boolean
    suspend fun createDirectory(parentUri: Uri, dirName: String): Uri?
    suspend fun getStorageStats(): StorageStats
    
    fun getFavorites(): Flow<List<FileItem>>
    suspend fun addFavorite(fileItem: FileItem)
    suspend fun removeFavorite(uri: String)
    suspend fun isFavorite(uri: String): Boolean
    
    fun getRecentFiles(): Flow<List<FileItem>>
    suspend fun addRecentFile(fileItem: FileItem)
    suspend fun clearRecentFiles()
    
    suspend fun moveToRecycleBin(fileUri: Uri, originalPath: String): Boolean
    suspend fun getRecycleBinFiles(): List<FileItem>
    suspend fun restoreFromRecycleBin(fileUri: Uri): Boolean
    suspend fun emptyRecycleBin(): Boolean
}
