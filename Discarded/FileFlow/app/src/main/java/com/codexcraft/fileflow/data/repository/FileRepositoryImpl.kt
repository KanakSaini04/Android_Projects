package com.codexcraft.fileflow.data.repository

import android.net.Uri
import com.codexcraft.fileflow.data.source.SafStorageDataSource
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.repository.FileRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class FileRepositoryImpl @Inject constructor(
    private val safStorageDataSource: SafStorageDataSource,
    @Named("io") private val ioDispatcher: CoroutineContext
) : FileRepository {

    override suspend fun getFiles(parentUri: Uri): Result<List<FileItem>> = withContext(ioDispatcher) {
        runCatching {
            safStorageDataSource.listFiles(parentUri).map { doc ->
                FileItem(
                    uri = doc.uri,
                    name = doc.name ?: "Unknown",
                    mimeType = doc.type,
                    isDirectory = doc.isDirectory,
                    sizeBytes = if (doc.isDirectory) 0 else doc.length(),
                    lastModified = doc.lastModified()
                )
            }
        }
    }
}
