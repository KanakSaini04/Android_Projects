package com.codexcraft.fileflow.data.local.db.dao

import androidx.room.*
import com.codexcraft.fileflow.data.local.db.entities.FavoriteFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteFileDao {
    @Query("SELECT * FROM favorite_files ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(file: FavoriteFileEntity)

    @Delete
    suspend fun removeFavorite(file: FavoriteFileEntity)

    @Query("DELETE FROM favorite_files WHERE uri = :uri")
    suspend fun removeFavoriteByUri(uri: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_files WHERE uri = :uri)")
    suspend fun isFavorite(uri: String): Boolean
}
