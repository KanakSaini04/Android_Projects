package com.vidflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidflow.data.api.RetrofitClient
import com.vidflow.data.api.model.VideoInfoResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class HomeUiState(
    val url: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

object VideoInfoHolder {
    var info: VideoInfoResponse? = null
}

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    fun onUrlChange(url: String) {
        _state.update { it.copy(url = url, error = null) }
    }

    fun fetchInfo(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val info = RetrofitClient.api.getVideoInfo(_state.value.url)
                VideoInfoHolder.info = info
                _state.update { it.copy(loading = false) }
                onSuccess()
            } catch (e: IOException) {
                _state.update { it.copy(loading = false, error = "Network error. Check connection.") }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = "Invalid or unsupported URL") }
            }
        }
    }
}