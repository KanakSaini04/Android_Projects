package com.lensora.domain.usecase.gallery

import com.lensora.domain.model.GalleryPhoto
import com.lensora.domain.repository.GalleryRepository
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(private val repo: GalleryRepository) {
    suspend operator fun invoke() = repo.getPhotos()
}
class GetBestShotsUseCase @Inject constructor(private val repo: GalleryRepository) {
    suspend operator fun invoke(photos: List<GalleryPhoto>) = repo.getBestShots(photos)
}
class DeletePhotosUseCase @Inject constructor(private val repo: GalleryRepository) {
    suspend operator fun invoke(ids: List<String>) = repo.deletePhotos(ids)
}
class GetHighlightsUseCase @Inject constructor(private val repo: GalleryRepository) {
    suspend operator fun invoke(photos: List<GalleryPhoto>) = repo.getHighlights(photos)
}
