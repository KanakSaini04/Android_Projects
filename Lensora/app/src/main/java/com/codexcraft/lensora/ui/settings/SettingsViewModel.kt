package com.codexcraft.lensora.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.lensora.data.model.UserProfile
import com.codexcraft.lensora.data.repository.AuthRepository
import com.codexcraft.lensora.data.repository.SettingsRepository
import com.codexcraft.lensora.domain.usecase.AiCameraMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = authRepository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val isMirrorSync: StateFlow<Boolean> = settingsRepository.isMirrorSyncEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isFind: StateFlow<Boolean> = settingsRepository.isFindEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setMirrorSync(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setMirrorSync(enabled) }
    }

    fun setFind(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setFind(enabled) }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }
}