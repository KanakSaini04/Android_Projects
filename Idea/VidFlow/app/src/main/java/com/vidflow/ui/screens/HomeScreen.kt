package com.vidflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vidflow.navigation.Screen
import com.vidflow.ui.theme.Green400
import com.vidflow.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    vm: HomeViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val focusManager = LocalFocusManager.current
    var url by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Title
        Text(
            text = "VidFlow",
            style = MaterialTheme.typography.headlineLarge,
            color = Green400
        )
        Text(
            text = "Paste any video URL to download",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
        )

        // URL Input
        OutlinedTextField(
            value = url,
            onValueChange = { url = it; vm.onUrlChange(it) },
            label = { Text("Video URL") },
            leadingIcon = { Icon(Icons.Rounded.Link, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            isError = state.error != null,
            supportingText = {
                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Green400,
                focusedLabelColor = Green400,
                focusedLeadingIconColor = Green400
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fetch Button
        Button(
            onClick = {
                focusManager.clearFocus()
                if (url.isBlank()) {
                    vm.onUrlChange("")
                } else if (!url.startsWith("http")) {
                    vm.onUrlChange("invalid")
                } else {
                    vm.fetchInfo {
                        navController.navigate(Screen.Preview.route)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            enabled = !state.loading,
            colors = ButtonDefaults.buttonColors(containerColor = Green400)
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.background,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    "Fetch Info",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}