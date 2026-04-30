package com.vidflow.data.db

import androidx.room.*
import com.vidflow.data.db.entity.DownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(download: DownloadEntity): Long

    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAll(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status IN ('queued','downloading','failed') ORDER BY timestamp DESC")
    fun getActive(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status = 'completed' ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<DownloadEntity>>

    @Query("UPDATE downloads SET status = :status, filePath = :filePath WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, filePath: String?)

    @Query("UPDATE downloads SET progress = :progress, status = :status WHERE id = :id")
    suspend fun updateProgress(id: Long, progress: Int, status: String)

    @Delete
    suspend fun delete(download: DownloadEntity)
}