package com.codexcraft.lensora.domain.usecase

import com.codexcraft.lensora.data.model.CapturedMedia
import com.codexcraft.lensora.data.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMediaVaultUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(): Flow<List<CapturedMedia>> = mediaRepository.mediaList

    suspend fun load() = mediaRepository.loadMedia()
}