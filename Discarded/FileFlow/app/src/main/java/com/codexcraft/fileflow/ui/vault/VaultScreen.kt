package com.codexcraft.fileflow.ui.vault

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.codexcraft.fileflow.core.util.UriUtils
import com.codexcraft.fileflow.domain.repository.VaultItem

@Composable
fun VaultScreen(viewModel: VaultViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var isUnlocked by remember { mutableStateOf(false) }
    val vaultItems by viewModel.items.collectAsState()
    var selectedItemForExport by remember { mutableStateOf<VaultItem?>(null) }

    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val name = UriUtils.getFileName(context, uri)
            viewModel.encryptFile(uri, name)
            Toast.makeText(context, "Encrypted to Vault", Toast.LENGTH_SHORT).show()
        }
    }

    val exportPicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("*/*")) { uri ->
        if (uri != null && selectedItemForExport != null) {
            viewModel.decryptFile(selectedItemForExport!!, uri)
            Toast.makeText(context, "Exported Successfully", Toast.LENGTH_SHORT).show()
            selectedItemForExport = null
        }
    }

    fun showBiometricPrompt() {
        val activity = context as? FragmentActivity ?: run {
            Toast.makeText(context, "Biometric unlock requires FragmentActivity", Toast.LENGTH_SHORT).show()
            return
        }
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isUnlocked = true
                    viewModel.loadVault()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Secure Vault")
            .setSubtitle("Authenticate to access your encrypted files")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    if (!isUnlocked) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.LockOpen,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = { showBiometricPrompt() }) {
                Text("Unlock Secure Vault")
            }
        }
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { filePicker.launch(arrayOf("*/*")) }) {
                    Icon(Icons.Default.Add, "Add to Vault")
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Secure Vault", style = MaterialTheme.typography.headlineMedium)
                    Button(onClick = { isUnlocked = false }) { Text("Lock") }
                }
                Spacer(Modifier.height(16.dp))
                if (vaultItems.isEmpty()) {
                    Text("Vault is empty. Add files using the + button.", color = MaterialTheme.colorScheme.tertiary)
                } else {
                    LazyColumn {
                        items(vaultItems) { item ->
                            Row(
                                Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🔒", modifier = Modifier.width(32.dp))
                                Text(item.name, modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    selectedItemForExport = item
                                    exportPicker.launch(item.name)
                                }) {
                                    Icon(Icons.Default.LockOpen, "Decrypt & Export")
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
