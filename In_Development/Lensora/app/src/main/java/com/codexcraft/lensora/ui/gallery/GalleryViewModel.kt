package com.codexcraft.lensora.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.lensora.data.model.CapturedMedia
import com.codexcraft.lensora.domain.usecase.GetMediaVaultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GalleryAuthState {
    data object Locked : GalleryAuthState()
    data object Unlocked : GalleryAuthState()
    data object Failed : GalleryAuthState()
}

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val getMediaVaultUseCase: GetMediaVaultUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<GalleryAuthState>(GalleryAuthState.Locked)
    val authState: StateFlow<GalleryAuthState> = _authState.asStateFlow()

    val mediaList: StateFlow<List<CapturedMedia>> = getMediaVaultUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onBiometricSuccess() {
        _authState.value = GalleryAuthState.Unlocked
    }

    fun onBiometricFailed() {
        _authState.value = GalleryAuthState.Failed
    }

    fun lock() {
        _authState.value = GalleryAuthState.Locked
    }

    fun loadMedia() {
        viewModelScope.launch {
            getMediaVaultUseCase.load()
        }
    }
}