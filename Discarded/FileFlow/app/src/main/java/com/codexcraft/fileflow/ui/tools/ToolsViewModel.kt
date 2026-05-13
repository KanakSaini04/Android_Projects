package com.codexcraft.fileflow.ui.tools

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.fileflow.domain.repository.ToolsRepository
import com.codexcraft.fileflow.domain.usecase.tools.FindDuplicatesUseCase
import com.codexcraft.fileflow.domain.usecase.tools.ImageToPdfUseCase
import com.codexcraft.fileflow.domain.usecase.tools.StartFlowShareUseCase
import com.codexcraft.fileflow.domain.usecase.tools.StopFlowShareUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val imageToPdfUseCase: ImageToPdfUseCase,
    private val startFlowShareUseCase: StartFlowShareUseCase,
    private val stopFlowShareUseCase: StopFlowShareUseCase,
    private val findDuplicatesUseCase: FindDuplicatesUseCase,
    private val toolsRepository: ToolsRepository
) : ViewModel() {
    fun createPdf(images: List<Uri>, output: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(imageToPdfUseCase(images, output))
        }
    }

    fun startFlowShare(onUrl: (String) -> Unit) {
        viewModelScope.launch {
            onUrl(startFlowShareUseCase(8080))
        }
    }

    fun stopFlowShare() {
        viewModelScope.launch { stopFlowShareUseCase() }
    }

    fun findDuplicates(onResult: (List<Pair<String, List<Uri>>>) -> Unit) {
        viewModelScope.launch {
            onResult(findDuplicatesUseCase())
        }
    }

    fun deleteFiles(uris: List<Uri>, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            onResult(toolsRepository.deleteFiles(uris))
        }
    }
}
