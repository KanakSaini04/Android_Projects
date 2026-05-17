package com.lensora.domain.repository

import android.net.Uri
import com.lensora.domain.model.CinematicFilter

interface EditorRepository {
    suspend fun aiEnhance(uri: Uri): Result<Uri>
    suspend fun applyFilter(uri: Uri, filter: CinematicFilter): Result<Uri>
    suspend fun savePhoto(uri: Uri): Result<Unit>
    suspend fun sharePhoto(uri: Uri)
}
