package com.codexcraft.fileflow.data.local.storage

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import com.codexcraft.fileflow.domain.model.FileCategory
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.model.StorageStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun getFiles(directoryUri: Uri): List<FileItem> = withContext(Dispatchers.IO) {
        try {
            val directory = DocumentFile.fromTreeUri(context, directoryUri)
                ?: return@withContext emptyList()
            
            directory.listFiles().mapNotNull { docFile ->
                try {
                    FileItem(
                        uri = docFile.uri,
                        name = docFile.name ?: "Unknown",
                        size = docFile.length(),
                        mimeType = docFile.type ?: "application/octet-stream",
                        lastModified = docFile.lastModified(),
                        isDirectory = docFile.isDirectory,
                        path = docFile.uri.path ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun deleteFile(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            DocumentsContract.deleteDocument(context.contentResolver, uri)
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun renameFile(uri: Uri, newName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            DocumentsContract.renameDocument(context.contentResolver, uri, newName) != null
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun moveFile(sourceUri: Uri, targetDirUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val sourceParentUri = DocumentFile.fromSingleUri(context, sourceUri)?.parentFile?.uri
                ?: return@withContext false
            
            DocumentsContract.moveDocument(
                context.contentResolver,
                sourceUri,
                sourceParentUri,
                targetDirUri
            ) != null
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun createDirectory(parentUri: Uri, dirName: String): Uri? = withContext(Dispatchers.IO) {
        try {
            val parent = DocumentFile.fromTreeUri(context, parentUri) ?: return@withContext null
            parent.createDirectory(dirName)?.uri
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun copyFile(sourceUri: Uri, targetDirUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val sourceFile = DocumentFile.fromSingleUri(context, sourceUri) ?: return@withContext false
            val targetDir = DocumentFile.fromTreeUri(context, targetDirUri) ?: return@withContext false
            
            val newFile = targetDir.createFile(
                sourceFile.type ?: "application/octet-stream",
                sourceFile.name ?: "copy"
            ) ?: return@withContext false
            
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                context.contentResolver.openOutputStream(newFile.uri)?.use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getStorageStats(): StorageStats = withContext(Dispatchers.IO) {
        try {
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            val totalSpace = stat.blockCountLong * stat.blockSizeLong
            val freeSpace = stat.availableBlocksLong * stat.blockSizeLong
            val usedSpace = totalSpace - freeSpace
            
            StorageStats(
                totalSpace = totalSpace,
                usedSpace = usedSpace,
                freeSpace = freeSpace
            )
        } catch (e: Exception) {
            StorageStats(0, 0, 0)
        }
    }
    
    suspend fun searchFiles(directoryUri: Uri, query: String): List<FileItem> = withContext(Dispatchers.IO) {
        getFiles(directoryUri).filter { 
            it.name.contains(query, ignoreCase = true) 
        }
    }
    
    fun getFilesByCategory(files: List<FileItem>, category: FileCategory): List<FileItem> {
        return files.filter { it.category == category }
    }
}
