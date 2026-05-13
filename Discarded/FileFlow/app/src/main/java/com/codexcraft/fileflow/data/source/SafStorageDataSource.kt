package com.codexcraft.fileflow.data.source

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface SafStorageDataSource {
    suspend fun listFiles(parentUri: Uri): List<DocumentFile>
    suspend fun move(sourceUri: Uri, targetParentUri: Uri): Boolean
    suspend fun delete(uri: Uri): Boolean
    suspend fun readBytes(uri: Uri): ByteArray
    suspend fun createFile(parentUri: Uri, mimeType: String, name: String): Uri?
    suspend fun writeBytes(uri: Uri, bytes: ByteArray): Boolean
    suspend fun createFolder(parentUri: Uri, name: String): Uri?
    suspend fun createDirectory(parentUri: Uri, name: String): Uri?
    suspend fun rename(uri: Uri, newName: String): Boolean
}

class SafStorageDataSourceImpl @Inject constructor(
    private val context: Context,
    private val contentResolver: ContentResolver
) : SafStorageDataSource {

    override suspend fun listFiles(parentUri: Uri): List<DocumentFile> = withContext(Dispatchers.IO) {
        try {
            val takeFlags: Int =
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(parentUri, takeFlags)

            val parentDocument = DocumentFile.fromTreeUri(context, parentUri)
            parentDocument?.listFiles()?.toList() ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun move(sourceUri: Uri, targetParentUri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val source = DocumentFile.fromSingleUri(context, sourceUri) ?: return@runCatching false
            val targetParent = DocumentFile.fromTreeUri(context, targetParentUri) ?: return@runCatching false
            val input = contentResolver.openInputStream(sourceUri)?.use { it.readBytes() } ?: return@runCatching false
            val created = targetParent.createFile(source.type ?: "application/octet-stream", source.name ?: "moved_file")
                ?: return@runCatching false
            val writeOk = contentResolver.openOutputStream(created.uri, "wt")?.use {
                it.write(input)
                it.flush()
                true
            } ?: false
            if (writeOk) source.delete()
            writeOk
        }.getOrDefault(false)
    }

    override suspend fun delete(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            DocumentFile.fromSingleUri(context, uri)?.delete() ?: false
        }.getOrDefault(false)
    }

    override suspend fun readBytes(uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
    }

    override suspend fun createFile(parentUri: Uri, mimeType: String, name: String): Uri? = withContext(Dispatchers.IO) {
        DocumentFile.fromTreeUri(context, parentUri)?.createFile(mimeType, name)?.uri
    }

    override suspend fun writeBytes(uri: Uri, bytes: ByteArray): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            contentResolver.openOutputStream(uri, "wt")?.use {
                it.write(bytes)
                it.flush()
                true
            } ?: false
        }.getOrDefault(false)
    }

    override suspend fun createFolder(parentUri: Uri, name: String): Uri? = withContext(Dispatchers.IO) {
        DocumentFile.fromTreeUri(context, parentUri)?.createDirectory(name)?.uri
    }

    override suspend fun createDirectory(parentUri: Uri, name: String): Uri? = createFolder(parentUri, name)

    override suspend fun rename(uri: Uri, newName: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            (DocumentFile.fromSingleUri(context, uri) ?: DocumentFile.fromTreeUri(context, uri))
                ?.renameTo(newName) ?: false
        }.getOrDefault(false)
    }
}
