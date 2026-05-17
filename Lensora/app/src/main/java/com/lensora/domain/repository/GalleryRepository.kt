package com.lensora.domain.repository

import com.lensora.domain.model.GalleryPhoto

interface GalleryRepository {
    suspend fun getPhotos(): List<GalleryPhoto>
    suspend fun deletePhotos(ids: List<String>)
    suspend fun getBestShots(photos: List<GalleryPhoto>): List<GalleryPhoto>
    suspend fun getHighlights(photos: List<GalleryPhoto>): List<String>
}
