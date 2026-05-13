package com.example.taskflow.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("taskId", -1)
        val taskTitle = intent.getStringExtra("taskTitle") ?: "Task Reminder"

        if (taskId != -1) {
            NotificationHelper.showNotification(context, taskId, taskTitle)
        }
    }
}