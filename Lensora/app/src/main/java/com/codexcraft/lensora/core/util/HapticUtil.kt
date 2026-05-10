package com.codexcraft.lensora.core.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.getSystemService

object HapticUtil {

    fun strongTick(context: Context) {
        vibrate(context, VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    fun doubleTick(context: Context) {
        vibrate(
            context,
            VibrationEffect.createWaveform(longArrayOf(0, 40, 60, 40), intArrayOf(0, 200, 0, 200), -1)
        )
    }

    fun lightClick(context: Context) {
        vibrate(context, VibrationEffect.createOneShot(20, 80))
    }

    private fun vibrate(context: Context, effect: VibrationEffect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService<VibratorManager>()
            vibratorManager?.defaultVibrator?.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService<Vibrator>()?.vibrate(effect)
        }
    }
}