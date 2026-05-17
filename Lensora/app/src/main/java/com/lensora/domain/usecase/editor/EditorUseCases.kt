package com.lensora.domain.usecase.editor

import android.net.Uri
import com.lensora.domain.model.CinematicFilter
import com.lensora.domain.repository.EditorRepository
import javax.inject.Inject

class AiEnhanceUseCase @Inject constructor(private val repo: EditorRepository) {
    suspend operator fun invoke(uri: Uri): Result<Uri> = repo.aiEnhance(uri)
}
class ApplyFilterUseCase @Inject constructor(private val repo: EditorRepository) {
    suspend operator fun invoke(uri: Uri, filter: CinematicFilter): Result<Uri> = repo.applyFilter(uri, filter)
}
class SavePhotoUseCase @Inject constructor(private val repo: EditorRepository) {
    suspend operator fun invoke(uri: Uri): Result<Unit> = repo.savePhoto(uri)
}
class SharePhotoUseCase @Inject constructor(private val repo: EditorRepository) {
    suspend operator fun invoke(uri: Uri) = repo.sharePhoto(uri)
}
