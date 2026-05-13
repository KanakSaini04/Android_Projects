package com.example.taskflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskflow.data.local.entity.Note
import com.example.taskflow.data.local.entity.NoteImage
import com.example.taskflow.data.local.entity.Task
import com.example.taskflow.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val tasks = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addTask(task: Task, onSaved: (Task) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insertTask(task)
            val savedTask = task.copy(id = id.toInt())
            onSaved(savedTask)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch { repository.updateTask(task) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun getNotesForTask(taskId: Int) =
        repository.getNotesForTask(taskId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addNote(note: Note, onSaved: (Note, Int) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            repository.insertNote(note)
            onSaved(note, 0)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch { repository.updateNote(note) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { repository.deleteNote(note) }
    }

    // ---------- Image Operations ----------

    fun getImagesForNote(noteId: Int) =
        repository.getImagesForNote(noteId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addImage(image: NoteImage) {
        viewModelScope.launch { repository.insertImage(image) }
    }

    fun deleteImage(image: NoteImage) {
        viewModelScope.launch { repository.deleteImage(image) }
    }
}