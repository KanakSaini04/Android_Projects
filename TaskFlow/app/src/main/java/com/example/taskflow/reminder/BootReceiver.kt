package com.example.taskflow.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.taskflow.data.local.db.FlowMindDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = FlowMindDatabase.getDatabase(context)
                val tasks = db.taskDao().getAllTasks().first()
                tasks.forEach { task ->
                    if (task.reminderTime != null &&
                        task.reminderTime > System.currentTimeMillis() &&
                        !task.isCompleted
                    ) {
                        ReminderScheduler.schedule(context, task)
                    }
                }
            }
        }
    }
}