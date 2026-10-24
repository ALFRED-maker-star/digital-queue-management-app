package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object QueueSoundEffects {

    fun playChime(context: Context) {
        try {
            // Play pleasant double chime
            Thread {
                try {
                    val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 250)
                    Thread.sleep(280)
                    toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 350)
                    Thread.sleep(400)
                    toneGen.release()
                } catch (ignored: Exception) {
                }
            }.start()

            // Gentle haptic pulse
            vibrate(context)
        } catch (ignored: Exception) {
        }
    }

    fun vibrate(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 200), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 200), -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(250)
                }
            }
        } catch (ignored: Exception) {
        }
    }
}
