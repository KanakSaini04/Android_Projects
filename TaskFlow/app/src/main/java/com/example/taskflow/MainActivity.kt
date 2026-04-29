package com.example.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.taskflow.presentation.viewmodel.TaskViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskflow.presentation.ui.NavGraph
import com.example.taskflow.presentation.viewmodel.TaskViewModelFactory
import com.example.taskflow.ui.theme.TaskFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as TaskFlowApp
        val factory = TaskViewModelFactory(app.repository)

        setContent {
            TaskFlowTheme {
                val viewModel: TaskViewModel = viewModel(factory = factory)
                NavGraph(viewModel = viewModel)
            }
        }
    }
}