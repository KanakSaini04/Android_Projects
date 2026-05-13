package com.codexcraft.caretap.ui.screens

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.codexcraft.caretap.ui.theme.Primary
import com.codexcraft.caretap.ui.viewmodel.ProfileViewModel

@Composable
fun AppsScreen(viewModel: ProfileViewModel) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Load installed apps once
    val apps = remember {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        context.packageManager
            .queryIntentActivities(intent, 0)
            .sortedBy { it.loadLabel(context.packageManager).toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Installed Apps",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(apps) { appInfo ->
                AppRow(
                    appInfo = appInfo,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val launchIntent = context.packageManager
                            .getLaunchIntentForPackage(
                                appInfo.activityInfo.packageName
                            )
                        launchIntent?.let { context.startActivity(it) }
                    }
                )
            }
        }
    }
}

@Composable
fun AppRow(
    appInfo: ResolveInfo,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val label = appInfo.loadLabel(context.packageManager).toString()
    val icon = appInfo.loadIcon(context.packageManager)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = icon.toBitmap(60, 60).asImageBitmap(),
                contentDescription = label,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}