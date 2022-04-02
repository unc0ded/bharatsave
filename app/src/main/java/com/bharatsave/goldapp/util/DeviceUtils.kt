package com.bharatsave.goldapp.util

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object DeviceUtils {

    enum class VibrationStrength(val strength: Long) {
        SOFT(50L),
        MEDIUM(100L),
        STRONG(200L)
    }

    @JvmStatic
    fun vibrateDevice(
        context: Context?,
        vibration: VibrationStrength = VibrationStrength.SOFT
    ) {

        context?.let {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    it.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                it.getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        vibration.strength,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(vibration.strength)
            }
        }
    }
}