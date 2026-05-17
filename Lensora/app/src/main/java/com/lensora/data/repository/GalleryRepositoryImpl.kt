package com.lensora.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.lensora.domain.model.GalleryPhoto
import com.lensora.domain.repository.GalleryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class GalleryRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : GalleryRepository {

    override suspend fun getPhotos(): List<GalleryPhoto> {
        val photos = mutableListOf<GalleryPhoto>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Lensora%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, selection, selectionArgs, sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val date = cursor.getLong(dateCol)
                photos.add(GalleryPhoto(id = id.toString(), uri = uri, timestamp = date))
            }
        }
        return photos
    }

    override suspend fun deletePhotos(ids: List<String>) {
        ids.forEach { id ->
            val uri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong()
            )
            context.contentResolver.delete(uri, null, null)
        }
    }

    override suspend fun getBestShots(photos: List<GalleryPhoto>): List<GalleryPhoto> {
        return photos.take(10).mapIndexed { index, photo ->
            photo.copy(isBestShot = index < 3)
        }
    }

    override suspend fun getHighlights(photos: List<GalleryPhoto>): List<String> {
        if (photos.isEmpty()) return emptyList()
        val today = photos.filter {
            val cal = Calendar.getInstance()
            val photoDate = Calendar.getInstance().apply { timeInMillis = it.timestamp * 1000 }
            cal.get(Calendar.DAY_OF_YEAR) == photoDate.get(Calendar.DAY_OF_YEAR)
        }
        return buildList {
            if (today.isNotEmpty()) add("📸 ${today.size} photos captured today")
            if (photos.size > 10) add("⭐ Best shots from your collection")
        }
    }
}
