package com.vidflow.worker

import android.content.Context
import android.os.Environment
import androidx.work.*
import com.vidflow.data.db.AppDatabase
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val url      = inputData.getString("url")      ?: return Result.failure()
        val title    = inputData.getString("title")    ?: "video"
        val id       = inputData.getLong("id", -1)
        val dao      = AppDatabase.getInstance(applicationContext).downloadDao()

        return try {
            val file = File(
                applicationContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                "${title.replace("/", "_")}.mp4"
            )

            val existingBytes = if (file.exists()) file.length() else 0L
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                if (existingBytes > 0) setRequestProperty("Range", "bytes=$existingBytes-")
                connect()
            }

            val total = connection.contentLength + existingBytes
            var downloaded = existingBytes

            FileOutputStream(file, existingBytes > 0).use { out ->
                connection.inputStream.use { input ->
                    val buffer = ByteArray(8192)
                    var bytes: Int
                    while (input.read(buffer).also { bytes = it } != -1) {
                        out.write(buffer, 0, bytes)
                        downloaded += bytes
                        val progress = if (total > 0) ((downloaded * 100) / total).toInt() else 0
                        dao.updateProgress(id, progress, "downloading")
                        setProgress(workDataOf("progress" to progress))
                    }
                }
            }

            dao.updateStatus(id, "completed", file.absolutePath)
            Result.success()
        } catch (e: Exception) {
            dao.updateStatus(id, "failed", null)
            Result.retry()
        }
    }

    companion object {
        fun buildRequest(id: Long, title: String, url: String): OneTimeWorkRequest {
            val data = workDataOf("id" to id, "title" to title, "url" to url)
            return OneTimeWorkRequestBuilder<DownloadWorker>()
                .setInputData(data)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build())
                .build()
        }
    }
}