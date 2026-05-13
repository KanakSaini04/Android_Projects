package com.vidflow.data.repository

import android.content.Context
import com.vidflow.data.db.AppDatabase
import com.vidflow.data.db.entity.DownloadEntity
import kotlinx.coroutines.flow.Flow

class DownloadRepository(context: Context) {
    private val dao = AppDatabase.getInstance(context).downloadDao()

    suspend fun insert(download: DownloadEntity): Long = dao.insert(download)
    fun getActive(): Flow<List<DownloadEntity>> = dao.getActive()
    fun getHistory(): Flow<List<DownloadEntity>> = dao.getHistory()
    fun getAll(): Flow<List<DownloadEntity>> = dao.getAll()
    suspend fun updateStatus(id: Long, status: String, filePath: String?) = dao.updateStatus(id, status, filePath)
    suspend fun updateProgress(id: Long, progress: Int, status: String) = dao.updateProgress(id, progress, status)
    suspend fun delete(download: DownloadEntity) = dao.delete(download)
}