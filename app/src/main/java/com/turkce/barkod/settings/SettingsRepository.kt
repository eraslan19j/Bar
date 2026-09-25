package com.turkce.barkod.settings

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppSettings(
    val ses: Boolean = true,
    val titresim: Boolean = true,
    val koyuTema: Boolean = true
)

class SettingsRepository(context: Context) {

    private val tercihler =
        context.applicationContext.getSharedPreferences("barkod_ayarlari", Context.MODE_PRIVATE)

    private val _ayarlar = MutableStateFlow(oku())
    val ayarlar: StateFlow<AppSettings> = _ayarlar.asStateFlow()

    private fun oku(): AppSettings = AppSettings(
        ses = tercihler.getBoolean(ANAHTAR_SES, true),
        titresim = tercihler.getBoolean(ANAHTAR_TITRESIM, true),
        koyuTema = tercihler.getBoolean(ANAHTAR_KOYU_TEMA, true)
    )

    private fun guncelle(yeni: AppSettings) {
        tercihler.edit {
            putBoolean(ANAHTAR_SES, yeni.ses)
            putBoolean(ANAHTAR_TITRESIM, yeni.titresim)
            putBoolean(ANAHTAR_KOYU_TEMA, yeni.koyuTema)
        }
        _ayarlar.value = yeni
    }

    fun sesAyariniDegistir(deger: Boolean) = guncelle(_ayarlar.value.copy(ses = deger))

    fun titresimAyariniDegistir(deger: Boolean) =
        guncelle(_ayarlar.value.copy(titresim = deger))

    fun koyuTemayiDegistir(deger: Boolean) =
        guncelle(_ayarlar.value.copy(koyuTema = deger))

    private companion object {
        const val ANAHTAR_SES = "ses"
        const val ANAHTAR_TITRESIM = "titresim"
        const val ANAHTAR_KOYU_TEMA = "koyu_tema"
    }
}
