package com.lensora.data.local

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("SELECT * FROM photos WHERE isBestShot = 1")
    fun getBestShots(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE tags LIKE '%' || :tag || '%'")
    fun getPhotosByTag(tag: String): Flow<List<PhotoEntity>>
}

@Database(entities = [PhotoEntity::class], version = 1, exportSchema = false)
abstract class LensoraDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao

    companion object {
        fun create(context: Context): LensoraDatabase {
            return Room.databaseBuilder(context, LensoraDatabase::class.java, "lensora_db").build()
        }
    }
}
