package com.vidflow.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class AppLockManager(private val context: Context) {

    fun isBiometricAvailable(): Boolean {
        val mgr = BiometricManager.from(context)
        return mgr.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onFallbackToPin: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        val prompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) { onSuccess() }

                override fun onAuthenticationError(code: Int, msg: CharSequence) {
                    onFallbackToPin()
                }

                override fun onAuthenticationFailed() { onFallbackToPin() }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("VidFlow Lock")
            .setSubtitle("Authenticate to continue")
            .setNegativeButtonText("Use PIN")
            .build()

        prompt.authenticate(info)
    }
}