package com.example.taskflow.data.repository

import com.example.taskflow.data.local.dao.NoteDao
import com.example.taskflow.data.local.dao.TaskDao
import com.example.taskflow.data.local.entity.Note
import com.example.taskflow.data.local.entity.NoteImage
import com.example.taskflow.data.local.entity.Task
import com.example.taskflow.data.local.dao.NoteImageDao
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val noteDao: NoteDao,
    private val noteImageDao: NoteImageDao
) {

    // ---------- Task Operations ----------

    suspend fun insertTask(task: Task): Long = taskDao.insert(task)
    suspend fun updateTask(task: Task) = taskDao.update(task)
    suspend fun deleteTask(task: Task) = taskDao.delete(task)
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    suspend fun getTaskById(id: Int): Task? = taskDao.getTaskById(id)

    // ---------- Note Operations ----------

    suspend fun insertNote(note: Note) = noteDao.insert(note)
    suspend fun updateNote(note: Note) = noteDao.update(note)
    suspend fun deleteNote(note: Note) = noteDao.delete(note)
    fun getNotesForTask(taskId: Int): Flow<List<Note>> = noteDao.getNotesForTask(taskId)

    // ---------- NoteImage Operations ----------

    suspend fun insertImage(image: NoteImage) = noteImageDao.insert(image)
    suspend fun deleteImage(image: NoteImage) = noteImageDao.delete(image)
    fun getImagesForNote(noteId: Int): Flow<List<NoteImage>> = noteImageDao.getImagesForNote(noteId)
}