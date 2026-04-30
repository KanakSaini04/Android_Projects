package com.vidflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.vidflow.data.api.model.Format
import com.vidflow.data.db.entity.DownloadEntity
import com.vidflow.data.repository.DownloadRepository
import com.vidflow.worker.DownloadWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DownloadViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = DownloadRepository(app)
    private val workManager = WorkManager.getInstance(app)

    val active = repo.getActive().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val history = repo.getHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun startDownload(title: String, thumbnail: String, format: Format) {
        viewModelScope.launch {
            val entity = DownloadEntity(
                title = title,
                thumbnail = thumbnail,
                quality = format.quality,
                downloadUrl = format.downloadUrl,
                status = "queued"
            )
            val id = repo.insert(entity)
            val request = DownloadWorker.buildRequest(id, title, format.downloadUrl)
            workManager.enqueue(request)
        }
    }

    fun retryDownload(download: DownloadEntity) {
        viewModelScope.launch {
            repo.updateStatus(download.id, "queued", null)
            val request = DownloadWorker.buildRequest(
                download.id, download.title, download.downloadUrl
            )
            workManager.enqueue(request)
        }
    }

    fun deleteDownload(download: DownloadEntity) {
        viewModelScope.launch { repo.delete(download) }
    }
}