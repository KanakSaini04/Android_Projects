package com.lensora.domain.usecase.settings

import com.lensora.domain.repository.SettingsRepository
import com.lensora.presentation.settings.SettingsUiState
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val repo: SettingsRepository) {
    suspend operator fun invoke(): SettingsUiState = repo.getSettings()
}
class SaveSettingsUseCase @Inject constructor(private val repo: SettingsRepository) {
    suspend operator fun invoke(settings: SettingsUiState) = repo.saveSettings(settings)
}
