package com.codexcraft.fileflow.data.local.storage

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.codexcraft.fileflow.domain.model.FileItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecycleBinManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val recycleBinDir = File(context.filesDir, "recycle_bin").apply { mkdirs() }
    
    suspend fun moveToRecycleBin(fileUri: Uri, originalPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val sourceFile = DocumentFile.fromSingleUri(context, fileUri) ?: return@withContext false
            val fileName = sourceFile.name ?: return@withContext false
            
            val timestamp = System.currentTimeMillis()
            val recycleBinFile = File(recycleBinDir, "${timestamp}_$fileName")
            
            context.contentResolver.openInputStream(fileUri)?.use { input ->
                recycleBinFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            // Store metadata
            File(recycleBinDir, "${timestamp}_${fileName}.meta").writeText(originalPath)
            
            // Delete original
            sourceFile.delete()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getRecycleBinFiles(): List<FileItem> = withContext(Dispatchers.IO) {
        recycleBinDir.listFiles()
            ?.filter { !it.name.endsWith(".meta") }
            ?.mapNotNull { file ->
                try {
                    FileItem(
                        uri = Uri.fromFile(file),
                        name = file.name.substringAfter("_"),
                        size = file.length(),
                        mimeType = "application/octet-stream",
                        lastModified = file.lastModified(),
                        isDirectory = false,
                        path = file.absolutePath
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
    }
    
    suspend fun restoreFromRecycleBin(fileUri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val recycleBinFile = File(fileUri.path ?: return@withContext false)
            val metaFile = File(recycleBinDir, "${recycleBinFile.name}.meta")
            
            if (!metaFile.exists()) return@withContext false
            
            val originalPath = metaFile.readText()
            val targetFile = File(originalPath)
            
            recycleBinFile.copyTo(targetFile, overwrite = true)
            recycleBinFile.delete()
            metaFile.delete()
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun emptyRecycleBin(): Boolean = withContext(Dispatchers.IO) {
        try {
            recycleBinDir.deleteRecursively()
            recycleBinDir.mkdirs()
            true
        } catch (e: Exception) {
            false
        }
    }
}
