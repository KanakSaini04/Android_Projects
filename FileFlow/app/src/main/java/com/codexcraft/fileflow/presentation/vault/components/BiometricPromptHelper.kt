package com.codexcraft.fileflow.presentation.vault.components

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.codexcraft.fileflow.util.BiometricHelper

@Composable
fun rememberBiometricPrompt(
    onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
    onError: (Int, CharSequence) -> Unit,
    onFailed: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    return remember(activity) {
        {
            activity?.let {
                BiometricHelper.showBiometricPrompt(
                    activity = it,
                    onSuccess = onSuccess,
                    onError = onError,
                    onFailed = onFailed
                )
            }
        }
    }
}
