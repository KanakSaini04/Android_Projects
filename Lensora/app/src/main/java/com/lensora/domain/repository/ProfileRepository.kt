package com.lensora.domain.repository

import com.lensora.domain.model.UserProfile

interface ProfileRepository {
    suspend fun getProfile(): UserProfile
    suspend fun updateProfile(name: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun getPresets(): List<String>
    suspend fun savePreset(name: String)
    suspend fun deletePreset(name: String)
}
