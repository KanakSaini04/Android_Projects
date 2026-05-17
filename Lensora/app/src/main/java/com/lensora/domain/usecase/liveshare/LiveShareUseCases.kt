package com.lensora.domain.usecase.liveshare

import android.graphics.Bitmap
import com.lensora.domain.repository.LiveShareRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StartLiveShareSessionUseCase @Inject constructor(private val repo: LiveShareRepository) {
    suspend operator fun invoke(): Result<String> = repo.startSession()
}
class StopLiveShareSessionUseCase @Inject constructor(private val repo: LiveShareRepository) {
    suspend operator fun invoke() = repo.stopSession()
}
class JoinLiveShareSessionUseCase @Inject constructor(private val repo: LiveShareRepository) {
    suspend operator fun invoke(code: String): Result<Unit> = repo.joinSession(code)
}
class GenerateQrCodeUseCase @Inject constructor(private val repo: LiveShareRepository) {
    suspend operator fun invoke(code: String): Bitmap = repo.generateQrCode(code)
}
class ObserveViewersUseCase @Inject constructor(private val repo: LiveShareRepository) {
    operator fun invoke(code: String): Flow<Int> = repo.observeViewers(code)
}
