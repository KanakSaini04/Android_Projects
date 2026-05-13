package com.codexcraft.fileflow.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.codexcraft.fileflow.app.navigation.Routes

@Composable
fun ToolsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Tools (Power-Ups)",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Button(onClick = { navController.navigate(Routes.IMAGE_TO_PDF) }) { Text("Image-to-PDF") }
        Button(onClick = { navController.navigate(Routes.FLOW_SHARE) }) { Text("FlowShare (WiFi Server)") }
        Button(onClick = { navController.navigate(Routes.CLEANER) }) { Text("Cleaner (Duplicates / Cache)") }
    }
}
