package com.example.taskflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val deadline: Long? = null,
    val priority: Int = 1,
    val isCompleted: Boolean = false,
    val reminderTime: Long? = null,
    val isSmartReminder: Boolean = false
)