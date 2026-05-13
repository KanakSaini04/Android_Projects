package com.codexcraft.fileflow.ui.reader

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.fileflow.domain.repository.ReaderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val readerRepository: ReaderRepository
) : ViewModel() {
    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _pageCount = MutableStateFlow(0)
    val pageCount = _pageCount.asStateFlow()

    fun loadText(uri: Uri) {
        viewModelScope.launch {
            _text.value = readerRepository.readText(uri)
        }
    }

    fun saveText(uri: Uri, value: String) {
        viewModelScope.launch {
            readerRepository.writeText(uri, value)
        }
    }

    fun loadPdf(uri: Uri) {
        viewModelScope.launch {
            _pageCount.value = readerRepository.getPdfPageCount(uri)
        }
    }

    suspend fun renderPdfPage(uri: Uri, index: Int, w: Int, h: Int): Bitmap {
        return readerRepository.renderPdfPage(uri, index, w, h)
    }
}
