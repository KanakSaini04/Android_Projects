package com.codexcraft.fileflow.domain.usecase.vault

import android.content.Context
import com.codexcraft.fileflow.util.BiometricHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ValidateBiometricUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): Boolean {
        return BiometricHelper.isBiometricAvailable(context)
    }
}
