package com.lensora.domain.repository

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface LiveShareRepository {
    suspend fun startSession(): Result<String>
    suspend fun stopSession()
    suspend fun joinSession(code: String): Result<Unit>
    suspend fun generateQrCode(code: String): Bitmap
    fun observeViewers(code: String): Flow<Int>
}
