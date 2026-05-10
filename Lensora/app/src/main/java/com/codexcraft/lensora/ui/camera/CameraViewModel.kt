package com.codexcraft.lensora.ui.camera

import android.content.Context
import androidx.camera.core.*
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codexcraft.lensora.core.util.createMediaFile
import com.codexcraft.lensora.data.repository.MediaRepository
import com.codexcraft.lensora.domain.usecase.AiCameraMode
import com.codexcraft.lensora.domain.usecase.AnalyzeSceneUseCase
import com.codexcraft.lensora.ui.camera.analyzer.ScenePulseAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val analyzeSceneUseCase: AnalyzeSceneUseCase,
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val scenePulseAnalyzer = ScenePulseAnalyzer(analyzeSceneUseCase)

    val aiMode: StateFlow<AiCameraMode> = scenePulseAnalyzer.aiMode
    val inferenceTimeMs: StateFlow<Long> = scenePulseAnalyzer.inferenceTimeMs
    val modeSwitchCount: StateFlow<Int> = scenePulseAnalyzer.modeSwitchCount

    private val _isLensFacingFront = MutableStateFlow(false)
    val isLensFacingFront: StateFlow<Boolean> = _isLensFacingFront.asStateFlow()

    private val _captureEvent = MutableSharedFlow<CaptureResult>()
    val captureEvent: SharedFlow<CaptureResult> = _captureEvent.asSharedFlow()

    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null

    fun flipCamera() {
        _isLensFacingFront.value = !_isLensFacingFront.value
    }

    fun bindCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()
            cameraProvider = provider

            val lensFacing = if (_isLensFacingFront.value)
                CameraSelector.LENS_FACING_FRONT
            else
                CameraSelector.LENS_FACING_BACK

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            val capture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
            imageCapture = capture

            val analysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor, scenePulseAnalyzer) }

            // Attempt HDR extension
            val extensionsFuture = ExtensionsManager.getInstanceAsync(context, provider)
            extensionsFuture.addListener({
                try {
                    val extManager = extensionsFuture.get()
                    val extSelector = if (extManager.isExtensionAvailable(cameraSelector, ExtensionMode.HDR)) {
                        extManager.getExtensionEnabledCameraSelector(cameraSelector, ExtensionMode.HDR)
                    } else {
                        cameraSelector
                    }

                    provider.unbindAll()
                    try {
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            extSelector,
                            preview,
                            capture,
                            analysis
                        )
                    } catch (e: Exception) {
                        // Fallback without extensions
                        provider.unbindAll()
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            capture,
                            analysis
                        )
                    }
                } catch (e: Exception) {
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        capture,
                        analysis
                    )
                }
            }, ContextCompat.getMainExecutor(context))

        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhoto(context: Context) {
        val capture = imageCapture ?: return
        val file = context.createMediaFile("jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    viewModelScope.launch {
                        mediaRepository.addMedia(file, aiMode.value.label)
                        _captureEvent.emit(CaptureResult.Success(file.absolutePath))
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    viewModelScope.launch {
                        _captureEvent.emit(CaptureResult.Error(exception.message ?: "Capture failed"))
                    }
                }
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
        cameraProvider?.unbindAll()
    }
}

sealed class CaptureResult {
    data class Success(val path: String) : CaptureResult()
    data class Error(val message: String) : CaptureResult()
}