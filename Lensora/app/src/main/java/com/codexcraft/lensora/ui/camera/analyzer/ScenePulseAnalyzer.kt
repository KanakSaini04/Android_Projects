package com.codexcraft.lensora.ui.camera.analyzer

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.codexcraft.lensora.core.util.toBitmap
import com.codexcraft.lensora.core.util.Constants
import com.codexcraft.lensora.domain.usecase.AiCameraMode
import com.codexcraft.lensora.domain.usecase.AnalyzeSceneUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScenePulseAnalyzer(
    private val analyzeSceneUseCase: AnalyzeSceneUseCase
) : ImageAnalysis.Analyzer {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _aiMode = MutableStateFlow(AiCameraMode.AUTO)
    val aiMode: StateFlow<AiCameraMode> = _aiMode.asStateFlow()

    private val _inferenceTimeMs = MutableStateFlow(0L)
    val inferenceTimeMs: StateFlow<Long> = _inferenceTimeMs.asStateFlow()

    private val _modeSwitchCount = MutableStateFlow(0)
    val modeSwitchCount: StateFlow<Int> = _modeSwitchCount.asStateFlow()

    private var lastAnalyzedTime = 0L
    private var latestBitmap: Bitmap? = null

    init {
        // Launch pulse loop: analyze every 10s
        scope.launch {
            while (true) {
                delay(Constants.AI_PULSE_INTERVAL_MS)
                latestBitmap?.let { bmp ->
                    val start = System.currentTimeMillis()
                    val newMode = analyzeSceneUseCase(bmp)
                    val elapsed = System.currentTimeMillis() - start
                    _inferenceTimeMs.value = elapsed
                    if (newMode != _aiMode.value) {
                        _aiMode.value = newMode
                        _modeSwitchCount.value++
                    }
                }
            }
        }
    }

    override fun analyze(image: ImageProxy) {
        image.use {
            val bmp = image.toBitmap()
            if (bmp != null) {
                latestBitmap = bmp
            }
        }
    }
}