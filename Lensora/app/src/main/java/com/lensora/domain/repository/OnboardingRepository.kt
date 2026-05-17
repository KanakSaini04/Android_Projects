package com.lensora.domain.repository

interface OnboardingRepository {
    suspend fun setOnboardingCompleted()
    suspend fun isOnboardingCompleted(): Boolean
}
