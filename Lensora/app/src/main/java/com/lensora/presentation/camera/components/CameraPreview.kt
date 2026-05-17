package com.lensora.presentation.camera.components

import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.lensora.domain.model.CinematicFilter
import jp.co.cyberagent.android.gpuimage.filter.*

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    currentFilter: CinematicFilter,
    onFrameAnalyzed: (ImageProxy) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            { imageProxy -> onFrameAnalyzed(imageProxy) }
                        )
                    }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

fun mapFilterToGPUFilter(filter: CinematicFilter): GPUImageFilter {
    return when (filter) {
        CinematicFilter.GOLDEN_HOUR -> GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                1.2f, 0.1f, 0.0f, 0.0f,
                0.1f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.8f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        )
        CinematicFilter.TOKYO_NIGHT -> GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0.8f, 0.0f, 0.2f, 0.0f,
                0.0f, 0.8f, 0.2f, 0.0f,
                0.0f, 0.0f, 1.3f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        )
        CinematicFilter.SOFT_PORTRAIT -> GPUImageContrastFilter(1.1f) // Replaced missing filter
        CinematicFilter.MOUNTAIN_AIR -> GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                0.9f, 0.0f, 0.1f, 0.0f,
                0.0f, 1.1f, 0.0f, 0.0f,
                0.1f, 0.1f, 1.2f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        )
        CinematicFilter.MOODY_STREET -> GPUImageColorMatrixFilter(
            1.0f,
            floatArrayOf(
                1.1f, 0.0f, 0.0f, -0.05f,
                0.0f, 1.0f, 0.0f, -0.05f,
                0.0f, 0.0f, 1.1f, -0.05f,
                0.0f, 0.0f, 0.0f, 1.0f
            )
        )
        else -> GPUImageContrastFilter(1.2f)
    }
}
