package com.lensora.domain.repository

import com.lensora.presentation.settings.SettingsUiState

interface SettingsRepository {
    suspend fun getSettings(): SettingsUiState
    suspend fun saveSettings(settings: SettingsUiState)
}
