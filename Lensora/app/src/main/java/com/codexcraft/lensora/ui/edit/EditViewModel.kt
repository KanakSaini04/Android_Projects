package com.codexcraft.lensora.ui.edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject

data class EditToolState(
    val magicEraserReady: Boolean = false,
    val relightReady: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class EditViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(EditToolState(isLoading = true))
    val state: StateFlow<EditToolState> = _state.asStateFlow()

    private var magicEraserInterpreter: Interpreter? = null
    private var relightInterpreter: Interpreter? = null

    fun initializeTfLiteModels(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            val eraserReady = withContext(Dispatchers.IO) {
                try {
                    val buffer = loadModelFile(context, "magic_eraser.tflite")
                    magicEraserInterpreter = Interpreter(buffer)
                    true
                } catch (e: Exception) {
                    false // Graceful: model file not yet present
                }
            }

            val relightReady = withContext(Dispatchers.IO) {
                try {
                    val buffer = loadModelFile(context, "relight.tflite")
                    relightInterpreter = Interpreter(buffer)
                    true
                } catch (e: Exception) {
                    false // Graceful: model file not yet present
                }
            }

            _state.value = EditToolState(
                magicEraserReady = eraserReady,
                relightReady = relightReady,
                isLoading = false,
                errorMessage = if (!eraserReady && !relightReady)
                    "AI models not loaded. Place .tflite files in assets/ to enable."
                else null
            )
        }
    }

    private fun loadModelFile(context: Context, fileName: String): MappedByteBuffer {
        val assetFd = context.assets.openFd(fileName)
        val stream = FileInputStream(assetFd.fileDescriptor)
        val channel = stream.channel
        return channel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFd.startOffset,
            assetFd.declaredLength
        )
    }

    override fun onCleared() {
        super.onCleared()
        magicEraserInterpreter?.close()
        relightInterpreter?.close()
    }
}