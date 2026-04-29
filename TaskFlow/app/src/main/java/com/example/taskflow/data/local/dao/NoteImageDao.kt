package com.example.taskflow.data.local.dao

import androidx.room.*
import com.example.taskflow.data.local.entity.NoteImage
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteImageDao {

    @Insert
    suspend fun insert(image: NoteImage)

    @Delete
    suspend fun delete(image: NoteImage)

    @Query("SELECT * FROM note_images WHERE noteId = :noteId")
    fun getImagesForNote(noteId: Int): Flow<List<NoteImage>>
}