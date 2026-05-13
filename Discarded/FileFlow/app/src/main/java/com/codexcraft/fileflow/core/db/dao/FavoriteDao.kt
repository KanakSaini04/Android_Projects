package com.codexcraft.fileflow.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codexcraft.fileflow.core.db.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: FavoriteEntity)

    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Query("DELETE FROM favorites WHERE uriString = :uriString")
    suspend fun remove(uriString: String)

    @Query("SELECT * FROM favorites WHERE uriString = :uriString LIMIT 1")
    suspend fun getByUri(uriString: String): FavoriteEntity?
}
