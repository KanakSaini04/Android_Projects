package com.vidflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vidflow.ui.theme.Green400
import com.vidflow.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    vm: SettingsViewModel = viewModel()
) {
    val theme by vm.theme.collectAsState()
    val appLock by vm.appLock.collectAsState()
    val biometric by vm.biometric.collectAsState()
    val defaultQuality by vm.defaultQuality.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("Settings", style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(24.dp))

        // Theme Section
        SettingsSectionLabel("Appearance")
        SettingsCard {
            Column {
                listOf("light", "dark", "system").forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            option.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        RadioButton(
                            selected = theme == option,
                            onClick = { vm.setTheme(option) },
                            colors = RadioButtonDefaults.colors(selectedColor = Green400)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Default Quality
        SettingsSectionLabel("Download")
        SettingsCard {
            Column {
                listOf("360p", "720p", "1080p").forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(option, style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface)
                        RadioButton(
                            selected = defaultQuality == option,
                            onClick = { vm.setDefaultQuality(option) },
                            colors = RadioButtonDefaults.colors(selectedColor = Green400)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Security
        SettingsSectionLabel("Security")
        SettingsCard {
            Column {
                SettingsToggleRow(
                    label = "App Lock",
                    icon = Icons.Rounded.Lock,
                    checked = appLock,
                    onCheckedChange = { vm.setAppLock(it) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
                SettingsToggleRow(
                    label = "Biometric",
                    icon = Icons.Rounded.Fingerprint,
                    checked = biometric,
                    enabled = appLock,
                    onCheckedChange = { vm.setBiometric(it) }
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun SettingsSectionLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = Green400,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) { content() }
}

@Composable
fun SettingsToggleRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null,
                tint = if (enabled) Green400 else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.background,
                checkedTrackColor = Green400)
        )
    }
}