package com.codexcraft.fileflow.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.model.StorageStats
import com.codexcraft.fileflow.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fileRepository: FileRepository
) : ViewModel() {
    
    private val _storageStats = MutableStateFlow(StorageStats(0, 0, 0))
    val storageStats: StateFlow<StorageStats> = _storageStats.asStateFlow()
    
    val recentFiles: StateFlow<List<FileItem>> = fileRepository.getRecentFiles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    init {
        loadStorageStats()
    }
    
    private fun loadStorageStats() {
        viewModelScope.launch {
            _storageStats.value = fileRepository.getStorageStats()
        }
    }
}
