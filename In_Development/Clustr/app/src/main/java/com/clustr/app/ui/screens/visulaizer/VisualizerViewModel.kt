package com.clustr.app.ui.screens.visualizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clustr.app.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlin.math.sin
import kotlin.math.PI
import kotlin.math.cos
import kotlin.random.Random

/**
 * UI State for the 3D Visualizer screen.
 * Optimized for 9:16 cinematic rendering with massive screen-filling nodes.
 */
data class VisualizerUiState(
    val nodes: List<VoiceNode> = emptyList(),
    val rotationY: Float = 0f,
    val isListening: Boolean = false,
    val micEnabled: Boolean = true,
    val isRecording: Boolean = false,
    val activeHz: Float = 0f,
    val activeAmplitude: Float = 0f,
    val nodeCount: Int = 0,
    val recordingMs: Long = 0L,
    val justSaved: Boolean = false
)

class VisualizerViewModel : ViewModel() {

    private val _state = MutableStateFlow(VisualizerUiState())
    val state: StateFlow<VisualizerUiState> = _state.asStateFlow()

    private val nodes = mutableListOf<VoiceNode>()
    private var audioJob: Job? = null
    private var animJob: Job? = null
    private var frameCount = 0L
    private var recordingStart = 0L

    private val sessionSnapshots = mutableListOf<NodeSnapshot>()

    companion object {
        const val MAX_NODES = 800 // Increased for denser wind/ambient visuals
        const val ROT_SPEED = 0.005f // Slightly slower rotation for a more cinematic feel
        const val FRAME_MS = 16L
    }

    fun setMicEnabled(enabled: Boolean) {
        _state.update { it.copy(micEnabled = enabled) }
        if (enabled) startListening() else stopListening()
    }

    fun startListening() {
        if (_state.value.isListening || !_state.value.micEnabled) return
        _state.update { it.copy(isListening = true) }
        startAnimLoop()

        audioJob = viewModelScope.launch {
            AudioEngine.audioStream().collect { frame ->
                // Lower threshold to 0.008f to capture subtle wind noise
                if (frame.amplitude > 0.008f) {
                    spawnNodes(frame)
                }
            }
        }
    }

    fun stopListening() {
        audioJob?.cancel()
        audioJob = null
        _state.update { it.copy(isListening = false) }
    }

    fun startRecording() {
        sessionSnapshots.clear()
        recordingStart = System.currentTimeMillis()
        _state.update { it.copy(isRecording = true, recordingMs = 0L) }
    }

    fun stopAndSaveRecording(title: String) {
        val durationMs = System.currentTimeMillis() - recordingStart
        _state.update { it.copy(isRecording = false) }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val peakHz = sessionSnapshots.maxOfOrNull { it.frequencyHz } ?: 0f
        val peakAmp = nodes.maxOfOrNull { it.life } ?: 0f

        val record = VoiceRecord(
            uid = uid,
            title = title.ifBlank { "Acoustic Pattern ${System.currentTimeMillis()}" },
            durationMs = durationMs,
            createdAt = Timestamp.now(),
            nodeSnapshots = sessionSnapshots.toList(),
            peakFrequencyHz = peakHz,
            peakAmplitude = peakAmp
        )

        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("recordings")
                    .add(record)
                    .await()

                _state.update { it.copy(justSaved = true) }
                delay(2000)
                _state.update { it.copy(justSaved = false) }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    private fun spawnNodes(frame: AudioFrame) {
        // Boosted spawn count for wind to create "airy" streams
        val spawnCount = (1 + (frame.amplitude * 5).toInt()).coerceAtMost(6)
        repeat(spawnCount) {
            if (nodes.size >= MAX_NODES) nodes.removeAt(0)
            val node = buildNode(frame)
            nodes.add(node)

            if (_state.value.isRecording) {
                sessionSnapshots.add(node.toSnapshot())
            }
        }
    }

    private fun buildNode(frame: AudioFrame): VoiceNode {
        val amp = frame.amplitude
        val hz = frame.frequency

        // SPATIAL LOGIC: filling the screen
        // Using polar coordinates with a much larger distance multiplier (500f)
        val angle = Random.nextFloat() * 2 * PI.toFloat()
        val distance = amp * 550f

        val x3d = cos(angle) * distance
        val y3d = sin(angle) * distance
        // Deep Z-axis creates a "tunnel" effect as nodes pass the camera
        val z3d = (Random.nextFloat() - 0.5f) * 600f

        return VoiceNode(
            x3d = x3d,
            y3d = y3d,
            z3d = z3d,
            // Massive radius to fill the 9:16 screen space
            radius = (20f + amp * 180f + Random.nextFloat() * 15f),
            color = frequencyToColor(hz),
            frequencyHz = hz,
            // Slower decay for wind (stays on screen longer)
            decay = (0.002f + (1f - amp) * 0.005f).coerceIn(0.001f, 0.01f),
            labelCountdown = if (Random.nextFloat() < 0.10f) 120 else 0
        )
    }

    private fun startAnimLoop() {
        if (animJob?.isActive == true) return
        animJob = viewModelScope.launch {
            while (isActive) {
                frameCount++
                val newRot = _state.value.rotationY + ROT_SPEED
                val recMs = if (_state.value.isRecording)
                    System.currentTimeMillis() - recordingStart else 0L

                val iter = nodes.iterator()
                while (iter.hasNext()) {
                    val n = iter.next()
                    n.life -= n.decay
                    if (n.labelCountdown > 0) n.labelCountdown--
                    if (n.life <= 0f) iter.remove()
                }

                val latest = nodes.lastOrNull()
                _state.update {
                    it.copy(
                        nodes = nodes.toList(),
                        rotationY = newRot,
                        activeHz = latest?.frequencyHz ?: 0f,
                        activeAmplitude = latest?.life ?: 0f,
                        nodeCount = nodes.size,
                        recordingMs = recMs
                    )
                }
                delay(FRAME_MS)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioJob?.cancel()
        animJob?.cancel()
    }
}

/**
 * Extension to convert a live VoiceNode to a saveable Snapshot.
 */
fun VoiceNode.toSnapshot() = NodeSnapshot(
    x = x3d,
    y = y3d,
    z = z3d,
    frequencyHz = frequencyHz,
    radius = radius
)