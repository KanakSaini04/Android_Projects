package com.codexcraft.fileflow.data.local.db.dao

import androidx.room.*
import com.codexcraft.fileflow.data.local.db.entities.RecentFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files ORDER BY accessedAt DESC LIMIT 20")
    fun getRecentFiles(): Flow<List<RecentFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecentFile(file: RecentFileEntity)

    @Query("DELETE FROM recent_files WHERE uri = :uri")
    suspend fun removeRecentFile(uri: String)

    @Query("DELETE FROM recent_files")
    suspend fun clearAllRecents()
}
