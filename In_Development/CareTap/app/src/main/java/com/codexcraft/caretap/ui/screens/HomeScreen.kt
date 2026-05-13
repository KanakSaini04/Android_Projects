package com.codexcraft.caretap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codexcraft.caretap.ui.components.AddProfileDialog
import com.codexcraft.caretap.ui.components.ProfileCard
import com.codexcraft.caretap.ui.theme.Primary
import com.codexcraft.caretap.ui.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(viewModel: ProfileViewModel) {
    val context = LocalContext.current
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "CareTap",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Tap a contact to connect",
                fontSize = 14.sp,
                color = Color(0xFF757575),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (profiles.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "👥", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No contacts yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF757575)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to add your first contact",
                            fontSize = 14.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
            } else {
                // 2-column grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(profiles, key = { it.id }) { profile ->
                        ProfileCard(
                            profile = profile,
                            onClick = {
                                viewModel.incrementUsage(profile.id)
                                val intent = android.content.Intent(
                                    context,
                                    com.codexcraft.caretap.ui.detail.ProfileDetailActivity::class.java
                                ).apply {
                                    putExtra(
                                        com.codexcraft.caretap.ui.detail.ProfileDetailActivity.EXTRA_PROFILE_ID,
                                        profile.id
                                    )
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }

        // FAB to add profile
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(64.dp),
            shape = CircleShape,
            containerColor = Primary,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Contact",
                modifier = Modifier.size(32.dp)
            )
        }
    }

    if (showAddDialog) {
        AddProfileDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone ->
                viewModel.addProfile(name, phone)
                showAddDialog = false
            }
        )
    }
}