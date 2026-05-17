package com.lensora.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator by lazy {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator
    }

    fun captureVibration() {
        vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun successVibration() {
        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 60, 30), -1))
    }

    fun errorVibration() {
        vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}

@Singleton
class OfflineManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isOffline() = !isOnline()
}

@Singleton
class GuestExpiryManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val GUEST_START_TIME = longPreferencesKey("guest_start_time")
        const val GUEST_TRIAL_DAYS = 7L
    }

    suspend fun isGuestExpired(): Boolean {
        val user = firebaseAuth.currentUser ?: return false
        if (!user.isAnonymous) return false
        val prefs = dataStore.data.first()
        val startTime = prefs[GUEST_START_TIME] ?: return false
        val elapsed = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - startTime)
        return elapsed >= GUEST_TRIAL_DAYS
    }

    suspend fun getRemainingDays(): Int {
        val prefs = dataStore.data.first()
        val startTime = prefs[GUEST_START_TIME] ?: return GUEST_TRIAL_DAYS.toInt()
        val elapsed = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - startTime)
        return maxOf(0, GUEST_TRIAL_DAYS.toInt() - elapsed.toInt())
    }
}
