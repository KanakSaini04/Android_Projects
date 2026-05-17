package com.codexcraft.fileflow.data.local.db.dao

import androidx.room.*
import com.codexcraft.fileflow.data.local.db.entities.VaultEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultEntryDao {
    @Query("SELECT * FROM vault_entries ORDER BY encryptedAt DESC")
    fun getAllVaultEntries(): Flow<List<VaultEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultEntry(entry: VaultEntryEntity)

    @Delete
    suspend fun deleteVaultEntry(entry: VaultEntryEntity)

    @Query("SELECT * FROM vault_entries WHERE id = :id")
    suspend fun getVaultEntryById(id: String): VaultEntryEntity?

    @Query("DELETE FROM vault_entries")
    suspend fun clearAllVaultEntries()
}
