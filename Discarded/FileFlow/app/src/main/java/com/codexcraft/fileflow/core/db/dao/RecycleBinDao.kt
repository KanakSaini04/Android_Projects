package com.codexcraft.fileflow.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codexcraft.fileflow.core.db.entity.RecycleBinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecycleBinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecycleBinEntity)

    @Query("SELECT * FROM recycle_bin ORDER BY deletedAt DESC")
    fun getAll(): Flow<List<RecycleBinEntity>>

    @Query("DELETE FROM recycle_bin WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM recycle_bin WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): RecycleBinEntity?
}
