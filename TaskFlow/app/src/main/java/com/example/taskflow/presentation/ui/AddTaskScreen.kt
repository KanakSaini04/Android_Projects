package com.example.taskflow.presentation.ui

import android.app.TimePickerDialog
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.taskflow.data.local.entity.Task
import com.example.taskflow.presentation.viewmodel.TaskViewModel
import com.example.taskflow.reminder.ReminderScheduler
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(1) }
    var titleError by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }

    val calendar = Calendar.getInstance()

    val timeFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    val timePicker = TimePickerDialog(
        context,
        { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            reminderTime = calendar.timeInMillis
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            timePicker.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                label = { Text("Task Title") },
                isError = titleError,
                supportingText = {
                    if (titleError) Text("Title cannot be empty")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Priority", style = MaterialTheme.typography.labelLarge)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1 to "Low", 2 to "Medium", 3 to "High").forEach { (value, label) ->
                    FilterChip(
                        selected = priority == value,
                        onClick = { priority = value },
                        label = { Text(label) }
                    )
                }
            }

            OutlinedButton(
                onClick = { datePicker.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (reminderTime != null)
                        "Reminder: ${timeFormatter.format(Date(reminderTime!!))}"
                    else
                        "Set Reminder (optional)"
                )
            }

            if (reminderTime != null) {
                TextButton(onClick = { reminderTime = null }) {
                    Text("Clear Reminder")
                }
            }

            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        val task = Task(
                            title = title.trim(),
                            priority = priority,
                            reminderTime = reminderTime
                        )
                        viewModel.addTask(task) { savedTask ->
                            if (savedTask.reminderTime != null) {
                                ReminderScheduler.schedule(context, savedTask)
                            }
                        }
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Task")
            }
        }
    }
}