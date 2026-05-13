package com.example.taskflow

import android.app.Application
import com.example.taskflow.data.local.db.FlowMindDatabase
import com.example.taskflow.data.repository.TaskRepository
import com.example.taskflow.reminder.NotificationHelper

class TaskFlowApp : Application() {

    val database by lazy {
        FlowMindDatabase.getDatabase(this)
    }

    val repository by lazy {
        TaskRepository(
            taskDao = database.taskDao(),
            noteDao = database.noteDao(),
            noteImageDao = database.noteImageDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}