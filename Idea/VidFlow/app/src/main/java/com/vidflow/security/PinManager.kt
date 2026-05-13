package com.vidflow.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PinManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "vidflow_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun savePin(pin: String) = prefs.edit().putString("pin", pin).apply()
    fun getPin(): String? = prefs.getString("pin", null)
    fun hasPin(): Boolean = getPin() != null
    fun clearPin() = prefs.edit().remove("pin").apply()
}