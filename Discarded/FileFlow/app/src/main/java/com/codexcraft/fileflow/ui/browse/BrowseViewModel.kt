package com.codexcraft.fileflow.ui.browse

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.fileflow.domain.model.FileItem
import com.codexcraft.fileflow.domain.repository.FileRepository
import com.codexcraft.fileflow.domain.usecase.browse.CreateFolderUseCase
import com.codexcraft.fileflow.domain.usecase.browse.DeleteFileUseCase
import com.codexcraft.fileflow.domain.usecase.browse.MoveFileUseCase
import com.codexcraft.fileflow.domain.usecase.browse.RenameFileUseCase
import com.codexcraft.fileflow.domain.usecase.metadata.AddRecentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BrowseState(
    val items: List<FileItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentParent: Uri? = null,
    val breadcrumbs: List<Pair<Uri, String>> = emptyList(),
    val showCreateFolderDialog: Boolean = false,
    val itemToRename: FileItem? = null
)

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val fileRepository: FileRepository,
    private val addRecentUseCase: AddRecentUseCase,
    private val moveFileUseCase: MoveFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    private val renameFileUseCase: RenameFileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BrowseState())
    val state = _state.asStateFlow()
    private val backStack = mutableListOf<Pair<Uri, String>>()

    fun load(parentUri: Uri, name: String = "Root") {
        val existingIndex = backStack.indexOfFirst { it.first == parentUri }
        if (existingIndex != -1) backStack.subList(existingIndex + 1, backStack.size).clear()
        else backStack.add(parentUri to name)

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    currentParent = parentUri,
                    error = null,
                    breadcrumbs = backStack.toList()
                )
            }
            fileRepository.getFiles(parentUri)
                .onSuccess { list ->
                    _state.update {
                        it.copy(
                            items = list.sortedBy { file -> !file.isDirectory },
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun openFile(item: FileItem) {
        viewModelScope.launch {
            addRecentUseCase(item.uri, item.name, item.mimeType)
        }
    }

    fun moveSelected(source: Uri, targetParent: Uri) {
        viewModelScope.launch {
            moveFileUseCase(source, targetParent)
            state.value.currentParent?.let { load(it, backStack.lastOrNull()?.second ?: "Root") }
        }
    }

    fun deleteSelected(item: FileItem) {
        viewModelScope.launch {
            val parent = state.value.currentParent ?: return@launch
            deleteFileUseCase(item.uri, parent, item.name, item.mimeType)
            load(parent, backStack.lastOrNull()?.second ?: "Root")
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            val parent = state.value.currentParent ?: return@launch
            createFolderUseCase(parent, name)
            load(parent, backStack.lastOrNull()?.second ?: "Root")
            _state.update { it.copy(showCreateFolderDialog = false) }
        }
    }

    fun renameItem(item: FileItem, newName: String) {
        viewModelScope.launch {
            renameFileUseCase(item.uri, newName)
            _state.update { it.copy(itemToRename = null) }
            state.value.currentParent?.let { load(it, backStack.lastOrNull()?.second ?: "Root") }
        }
    }

    fun toggleCreateDialog(show: Boolean) {
        _state.update { it.copy(showCreateFolderDialog = show) }
    }

    fun setRenameItem(item: FileItem?) {
        _state.update { it.copy(itemToRename = item) }
    }
}
