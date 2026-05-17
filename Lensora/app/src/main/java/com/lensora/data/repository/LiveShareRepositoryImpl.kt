package com.lensora.data.repository

import android.graphics.Bitmap
import android.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.lensora.domain.repository.LiveShareRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LiveShareRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LiveShareRepository {

    private var currentSessionId: String? = null

    override suspend fun startSession(): Result<String> {
        return try {
            val code = generateCode()
            val sessionData = hashMapOf(
                "code" to code,
                "createdAt" to System.currentTimeMillis(),
                "isActive" to true,
                "viewerCount" to 0
            )
            val doc = firestore.collection("live_sessions").add(sessionData).await()
            currentSessionId = doc.id
            Result.success(code)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun stopSession() {
        currentSessionId?.let { id ->
            try {
                firestore.collection("live_sessions").document(id)
                    .update("isActive", false).await()
            } catch (e: Exception) { e.printStackTrace() }
        }
        currentSessionId = null
    }

    override suspend fun joinSession(code: String): Result<Unit> {
        return try {
            val query = firestore.collection("live_sessions")
                .whereEqualTo("code", code)
                .whereEqualTo("isActive", true)
                .get().await()
            if (query.isEmpty) Result.failure(Exception("Session not found or expired"))
            else {
                val doc = query.documents.first()
                val currentCount = doc.getLong("viewerCount") ?: 0
                firestore.collection("live_sessions").document(doc.id)
                    .update("viewerCount", currentCount + 1).await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateQrCode(code: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    override fun observeViewers(code: String): Flow<Int> = callbackFlow {
        val listener = firestore.collection("live_sessions")
            .whereEqualTo("code", code)
            .addSnapshotListener { snapshot, _ ->
                val count = snapshot?.documents?.firstOrNull()?.getLong("viewerCount")?.toInt() ?: 0
                trySend(count)
            }
        awaitClose { listener.remove() }
    }

    private fun generateCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
