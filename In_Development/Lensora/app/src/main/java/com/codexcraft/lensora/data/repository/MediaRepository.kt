package com.codexcraft.lensora.data.repository

import android.content.Context
import com.codexcraft.lensora.data.model.CapturedMedia
import com.codexcraft.lensora.data.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _mediaList = MutableStateFlow<List<CapturedMedia>>(emptyList())
    val mediaList: Flow<List<CapturedMedia>> = _mediaList.asStateFlow()

    suspend fun loadMedia() = withContext(Dispatchers.IO) {
        val dir = context.getExternalFilesDir("Lensora") ?: return@withContext
        val files = dir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
        _mediaList.value = files.map { file ->
            CapturedMedia(
                id = UUID.randomUUID().toString(),
                file = file,
                timestamp = file.lastModified(),
                type = if (file.extension == "mp4") MediaType.VIDEO else MediaType.PHOTO
            )
        }
    }

    suspend fun addMedia(file: File, aiMode: String) {
        val media = CapturedMedia(
            id = UUID.randomUUID().toString(),
            file = file,
            timestamp = System.currentTimeMillis(),
            type = if (file.extension == "mp4") MediaType.VIDEO else MediaType.PHOTO,
            aiMode = aiMode
        )
        _mediaList.value = listOf(media) + _mediaList.value
    }

    suspend fun deleteMedia(media: CapturedMedia) = withContext(Dispatchers.IO) {
        media.file.delete()
        _mediaList.value = _mediaList.value.filter { it.id != media.id }
    }
}