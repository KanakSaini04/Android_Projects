package com.codexcraft.fileflow.presentation.reader

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _fileMetadata = MutableStateFlow<FileItem?>(null)
    val fileMetadata: StateFlow<FileItem?> = _fileMetadata.asStateFlow()

    fun loadFileMetadata(uri: Uri) {
        viewModelScope.launch {
            // In a real app, you'd fetch metadata for this specific URI
            // For now, we'll just create a dummy item or try to find it
            _fileMetadata.value = FileItem(
                uri = uri,
                name = uri.lastPathSegment ?: "Unknown File",
                size = 0L,
                mimeType = "application/octet-stream",
                lastModified = System.currentTimeMillis(),
                isDirectory = false,
                path = uri.path ?: ""
            )
            
            // Log access to recent files
            _fileMetadata.value?.let { 
                fileRepository.addRecentFile(it)
            }
        }
    }
}
