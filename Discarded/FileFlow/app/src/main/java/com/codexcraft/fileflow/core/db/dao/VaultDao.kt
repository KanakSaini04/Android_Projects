package com.codexcraft.fileflow.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codexcraft.fileflow.core.db.entity.VaultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VaultEntity)

    @Query("SELECT * FROM vault ORDER BY encryptedAt DESC")
    fun getAll(): Flow<List<VaultEntity>>

    @Query("DELETE FROM vault WHERE id = :id")
    suspend fun deleteById(id: String)
}
