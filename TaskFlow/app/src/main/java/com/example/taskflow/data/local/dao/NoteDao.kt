package com.example.taskflow.data.local.dao

import androidx.room.*
import com.example.taskflow.data.local.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM notes WHERE linkedTaskId = :taskId ORDER BY updatedAt DESC")
    fun getNotesForTask(taskId: Int): Flow<List<Note>>
}