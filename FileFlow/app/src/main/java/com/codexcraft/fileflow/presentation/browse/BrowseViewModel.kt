package com.codexcraft.fileflow.presentation.browse

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrowseUiState(
    val files: List<FileItem> = emptyList(),
    val currentDirectory: Uri? = null,
    val breadcrumbs: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSearchMode: Boolean = false,
    val searchQuery: String = "",
    val favorites: Set<String> = emptySet(),
    val showCreateDialog: Boolean = false
)

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val fileRepository: FileRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BrowseUiState())
    val uiState: StateFlow<BrowseUiState> = _uiState.asStateFlow()
    
    private val _selectedFiles = MutableStateFlow<Set<Uri>>(emptySet())
    val selectedFiles: StateFlow<Set<Uri>> = _selectedFiles.asStateFlow()
    
    private val directoryStack = mutableListOf<Uri>()
    
    init {
        observeFavorites()
    }
    
    private fun observeFavorites() {
        viewModelScope.launch {
            fileRepository.getFavorites().collect { favorites ->
                _uiState.update { it.copy(favorites = favorites.map { f -> f.uri.toString() }.toSet()) }
            }
        }
    }
    
    fun loadDirectory(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val files = if (_uiState.value.isSearchMode && _uiState.value.searchQuery.isNotBlank()) {
                    fileRepository.searchFiles(uri, _uiState.value.searchQuery)
                } else {
                    fileRepository.getFiles(uri)
                }
                
                if (!directoryStack.contains(uri)) {
                    directoryStack.add(uri)
                } else {
                    val index = directoryStack.indexOf(uri)
                    if (index != -1 && index < directoryStack.size - 1) {
                        directoryStack.subList(index + 1, directoryStack.size).clear()
                    }
                }
                
                _uiState.update {
                    it.copy(
                        files = files,
                        currentDirectory = uri,
                        breadcrumbs = updateBreadcrumbs(uri),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load directory"
                    )
                }
            }
        }
    }
    
    private fun updateBreadcrumbs(uri: Uri): List<String> {
        val path = uri.path ?: return listOf("Storage")
        // Simplified path display for SAF
        return path.split("/").filter { it.isNotBlank() && !it.contains(":") }.ifEmpty { listOf("Storage") }
    }
    
    fun navigateToBreadcrumb(index: Int) {
        if (index < directoryStack.size) {
            val targetUri = directoryStack[index]
            val itemsToRemove = directoryStack.size - 1 - index
            repeat(itemsToRemove) {
                directoryStack.removeAt(directoryStack.size - 1)
            }
            loadDirectory(targetUri)
        }
    }
    
    fun toggleSearchMode() {
        _uiState.update { 
            it.copy(
                isSearchMode = !it.isSearchMode,
                searchQuery = if (it.isSearchMode) "" else it.searchQuery
            ) 
        }
        if (!_uiState.value.isSearchMode) {
            _uiState.value.currentDirectory?.let { loadDirectory(it) }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _uiState.value.currentDirectory?.let { uri ->
            loadDirectory(uri)
        }
    }
    
    fun toggleFileSelection(uri: Uri) {
        _selectedFiles.update { selected ->
            if (selected.contains(uri)) {
                selected - uri
            } else {
                selected + uri
            }
        }
    }
    
    fun clearSelection() {
        _selectedFiles.value = emptySet()
    }
    
    fun deleteSelectedFiles() {
        viewModelScope.launch {
            val currentDir = _uiState.value.currentDirectory
            _selectedFiles.value.forEach { uri ->
                fileRepository.moveToRecycleBin(uri, uri.path ?: "")
            }
            clearSelection()
            currentDir?.let { loadDirectory(it) }
        }
    }
    
    fun copySelectedFiles() {
        // Implement copy logic if needed
        clearSelection()
    }
    
    fun toggleFavorite(file: FileItem) {
        viewModelScope.launch {
            if (_uiState.value.favorites.contains(file.uri.toString())) {
                fileRepository.removeFavorite(file.uri.toString())
            } else {
                fileRepository.addFavorite(file)
            }
        }
    }
    
    fun showCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }
    
    fun hideCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }
    
    fun createFolder(folderName: String) {
        viewModelScope.launch {
            _uiState.value.currentDirectory?.let { uri ->
                fileRepository.createDirectory(uri, folderName)
                loadDirectory(uri)
            }
        }
    }
}
