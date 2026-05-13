package com.clustr.app

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.sqrt

object AudioEngine {

    private const val SAMPLE_RATE = 44100
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(
        SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT
    ).coerceAtLeast(4096)

    private const val MAX_AMP = 32767f

    fun audioStream(): Flow<AudioFrame> = callbackFlow {
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE * 2
        )

        recorder.startRecording()
        val buffer = ShortArray(BUFFER_SIZE)

        try {
            while (!isClosedForSend) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    trySend(processBuffer(buffer, read))
                }
            }
        } finally {
            recorder.stop()
            recorder.release()
        }

        awaitClose {
            recorder.stop()
            recorder.release()
        }
    }.flowOn(Dispatchers.IO)

    private fun processBuffer(buffer: ShortArray, size: Int): AudioFrame {
        // RMS amplitude
        var sumSq = 0.0
        for (i in 0 until size) sumSq += (buffer[i] * buffer[i]).toDouble()
        val rms = sqrt(sumSq / size).toFloat()
        val amplitude = (rms / MAX_AMP).coerceIn(0f, 1f)

        // Zero-Crossing Rate → frequency estimate
        var crossings = 0
        for (i in 1 until size) {
            if ((buffer[i - 1] < 0 && buffer[i] >= 0) ||
                (buffer[i - 1] >= 0 && buffer[i] < 0)
            ) crossings++
        }
        val durationSec = size.toFloat() / SAMPLE_RATE
        val frequency = ((crossings / 2f) / durationSec).coerceIn(20f, 12000f)

        return AudioFrame(frequency, amplitude, rms)
    }
}