package com.codexcraft.fileflow.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codexcraft.fileflow.core.db.entity.RecentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentEntity)

    @Query("SELECT * FROM recents ORDER BY lastOpenedAt DESC LIMIT 20")
    fun getRecent(): Flow<List<RecentEntity>>

    @Query("DELETE FROM recents")
    suspend fun clear()
}
