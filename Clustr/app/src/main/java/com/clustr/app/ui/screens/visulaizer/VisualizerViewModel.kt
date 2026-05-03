package com.clustr.app.ui.screens.visualizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clustr.app.*
// Ensure this exists in Models.kt
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import kotlin.math.sin
import kotlin.random.Random

/**
 * UI State for the 3D Visualizer screen.
 * Optimized for 9:16 cinematic rendering.
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

    // Using a list for session snapshots to save to Firestore
    private val sessionSnapshots = mutableListOf<NodeSnapshot>()

    companion object {
        const val MAX_NODES = 600
        const val ROT_SPEED = 0.007f
        const val FRAME_MS = 16L // Targets ~60 FPS
    }

    /**
     * Updates the mic status based on UserProfile settings.
     */
    fun setMicEnabled(enabled: Boolean) {
        _state.update { it.copy(micEnabled = enabled) }
        if (enabled) startListening() else stopListening()
    }

    fun startListening() {
        if (_state.value.isListening || !_state.value.micEnabled) return
        _state.update { it.copy(isListening = true) }
        startAnimLoop()

        audioJob = viewModelScope.launch {
            // Collecting from the real-time AudioEngine stream
            AudioEngine.audioStream().collect { frame ->
                if (frame.amplitude > 0.012f) {
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

        // Calculate peaks for the recording summary
        val peakHz = sessionSnapshots.maxOfOrNull { it.frequencyHz } ?: 0f
        val peakAmp = nodes.maxOfOrNull { it.life } ?: 0f

        val record = VoiceRecord(
            uid = uid,
            title = title.ifBlank { "Voice Cluster ${System.currentTimeMillis()}" },
            durationMs = durationMs,
            createdAt = Timestamp.now(),
            nodeSnapshots = sessionSnapshots.toList(),
            peakFrequencyHz = peakHz,
            peakAmplitude = peakAmp
        )

        viewModelScope.launch {
            try {
                // Save to the Mumbai (asia-south1) Firestore instance
                FirebaseFirestore.getInstance()
                    .collection("recordings")
                    .add(record)
                    .await()

                _state.update { it.copy(justSaved = true) }
                delay(2000)
                _state.update { it.copy(justSaved = false) }
            } catch (e: Exception) {
                // Log error or handle UI feedback
            }
        }
    }

    private fun spawnNodes(frame: AudioFrame) {
        val spawnCount = (1 + (frame.amplitude * 3).toInt()).coerceAtMost(4)
        repeat(spawnCount) {
            if (nodes.size >= MAX_NODES) nodes.removeAt(0)
            val node = buildNode(frame)
            nodes.add(node)

            // If recording, capture a simplified snapshot of the 3D node
            if (_state.value.isRecording) {
                sessionSnapshots.add(node.toSnapshot())
            }
        }
    }

    private fun buildNode(frame: AudioFrame): VoiceNode {
        val amp = frame.amplitude
        val hz = frame.frequency

        // Normalizing frequency for X-axis placement
        val freqNorm = (hz - 80f) / 7920f

        // World-space coordinate logic for 3D projection
        val x3d = (freqNorm - 0.5f) * 360f + Random.nextFloat() * 80f - 40f
        val y3d = (Random.nextFloat() - 0.5f) * (100f + amp * 400f)
        val z3d = sin(frameCount * 0.04f + hz * 0.002f) * 100f + Random.nextFloat() * 80f - 40f

        return VoiceNode(
            x3d = x3d,
            y3d = y3d,
            z3d = z3d,
            radius = 6f + amp * 52f + Random.nextFloat() * 8f,
            color = frequencyToColor(hz),
            frequencyHz = hz,
            decay = (0.003f + (1f - amp) * 0.007f).coerceIn(0.002f, 0.014f),
            labelCountdown = if (Random.nextFloat() < 0.15f) 90 else 0
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

                // Update node lifetimes (Fade out)
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