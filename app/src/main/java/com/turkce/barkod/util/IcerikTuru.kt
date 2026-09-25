package com.turkce.barkod.util

import androidx.annotation.StringRes
import com.turkce.barkod.R

enum class IcerikTuru(val anahtar: String, @StringRes val etiket: Int) {
    URL("url", R.string.tur_url),
    EPOSTA("eposta", R.string.tur_eposta),
    TELEFON("telefon", R.string.tur_telefon),
    SMS("sms", R.string.tur_sms),
    KONUM("konum", R.string.tur_konum),
    WIFI("wifi", R.string.tur_wifi),
    METIN("metin", R.string.tur_metin),
    BILINMIYEN("bilinmeyen", R.string.tur_bilinmeyen);

    companion object {
        fun anahtardan(anahtar: String): IcerikTuru =
            entries.firstOrNull { it.anahtar == anahtar } ?: BILINMIYEN
    }
}
