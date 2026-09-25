package com.turkce.barkod.settings

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class FeedbackHelper(context: Context) {

    private val uygulamaBaglami = context.applicationContext

    private val toneGenerator: ToneGenerator? = runCatching {
        ToneGenerator(AudioManager.STREAM_SYSTEM, TON_SES_SEVIYESI)
    }.getOrNull()

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val yonetic = uygulamaBaglami.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
            as? VibratorManager
        yonetic?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        uygulamaBaglami.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun basariSesi() {
        runCatching {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, SES_SURESI_MS)
        }
    }

    fun titret() {
        runCatching {
            val cihaz = vibrator ?: return
            if (!cihaz.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cihaz.vibrate(VibrationEffect.createOneShot(TITRESIM_SURESI_MS, TITRESIM_GUCU))
            } else {
                @Suppress("DEPRECATION")
                cihaz.vibrate(TITRESIM_SURESI_MS)
            }
        }
    }

    fun kapat() {
        runCatching { toneGenerator?.release() }
    }

    private companion object {
        const val TON_SES_SEVIYESI = 80
        const val SES_SURESI_MS = 150
        const val TITRESIM_SURESI_MS = 90L
        const val TITRESIM_GUCU = 120
    }
}
