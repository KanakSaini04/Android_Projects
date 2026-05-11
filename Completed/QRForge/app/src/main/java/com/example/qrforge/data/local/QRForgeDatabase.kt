package com.example.qrforge.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rawValue: String,
    val type: String,
    val format: String,
    val timestamp: String,
    val isFavorite: Boolean = false,
    val isGenerated: Boolean = false
)

@Dao
interface ScanHistoryDao {

    @Query("SELECT * FROM scan_history ORDER BY id DESC")
    fun getAllHistory(): Flow<List<ScanHistoryEntity>>
    @Query("SELECT * FROM scan_history WHERE isGenerated = 0 ORDER BY id DESC LIMIT 1")
    suspend fun getLastScanned(): ScanHistoryEntity?
    @Query("""
        SELECT * FROM scan_history
        WHERE (:query = '' OR rawValue LIKE '%' || :query || '%')
        AND (:type = 'ALL' OR type = :type)
        ORDER BY
            CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END,
            id DESC
    """)
    fun getFilteredHistory(query: String, type: String): Flow<List<ScanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ScanHistoryEntity): Long

    @Update
    suspend fun update(entity: ScanHistoryEntity)

    @Delete
    suspend fun delete(entity: ScanHistoryEntity)

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()

    @Query("UPDATE scan_history SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: Long, fav: Boolean)
}

@Database(entities = [ScanHistoryEntity::class], version = 1, exportSchema = false)
abstract class QRForgeDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        const val DATABASE_NAME = "qrforge.db"
    }
}