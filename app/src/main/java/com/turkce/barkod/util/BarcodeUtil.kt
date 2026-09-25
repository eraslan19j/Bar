package com.turkce.barkod.util

import android.net.Uri
import androidx.annotation.StringRes
import com.google.mlkit.vision.barcode.common.Barcode
import com.turkce.barkod.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BarcodeUtil {

    @StringRes
    fun formatEtiketi(format: Int): Int = when (format) {
        Barcode.FORMAT_QR_CODE -> R.string.format_qr
        Barcode.FORMAT_EAN_13 -> R.string.format_ean13
        Barcode.FORMAT_EAN_8 -> R.string.format_ean8
        Barcode.FORMAT_UPC_A -> R.string.format_upca
        Barcode.FORMAT_UPC_E -> R.string.format_upce
        Barcode.FORMAT_CODE_128 -> R.string.format_code128
        Barcode.FORMAT_CODE_39 -> R.string.format_code39
        Barcode.FORMAT_CODE_93 -> R.string.format_code93
        Barcode.FORMAT_ITF -> R.string.format_itf
        Barcode.FORMAT_CODABAR -> R.string.format_codabar
        Barcode.FORMAT_PDF417 -> R.string.format_pdf417
        Barcode.FORMAT_AZTEC -> R.string.format_aztec
        Barcode.FORMAT_DATA_MATRIX -> R.string.format_datamatrix
        else -> R.string.format_bilinmeyen
    }

    fun formatKisaAdi(format: Int): String = when (format) {
        Barcode.FORMAT_QR_CODE -> "QR"
        Barcode.FORMAT_EAN_13 -> "EAN-13"
        Barcode.FORMAT_EAN_8 -> "EAN-8"
        Barcode.FORMAT_UPC_A -> "UPC-A"
        Barcode.FORMAT_UPC_E -> "UPC-E"
        Barcode.FORMAT_CODE_128 -> "CODE-128"
        Barcode.FORMAT_CODE_39 -> "CODE-39"
        Barcode.FORMAT_CODE_93 -> "CODE-93"
        Barcode.FORMAT_ITF -> "ITF"
        Barcode.FORMAT_CODABAR -> "CODABAR"
        Barcode.FORMAT_PDF417 -> "PDF417"
        Barcode.FORMAT_AZTEC -> "AZTEC"
        Barcode.FORMAT_DATA_MATRIX -> "DATA_MATRIX"
        else -> "BILINMIYEN"
    }

    fun formatKisaAdindan(ad: String): Int = when (ad.uppercase(Locale.US)) {
        "QR" -> Barcode.FORMAT_QR_CODE
        "EAN-13" -> Barcode.FORMAT_EAN_13
        "EAN-8" -> Barcode.FORMAT_EAN_8
        "UPC-A" -> Barcode.FORMAT_UPC_A
        "UPC-E" -> Barcode.FORMAT_UPC_E
        "CODE-128" -> Barcode.FORMAT_CODE_128
        "CODE-39" -> Barcode.FORMAT_CODE_39
        "CODE-93" -> Barcode.FORMAT_CODE_93
        "ITF" -> Barcode.FORMAT_ITF
        "CODABAR" -> Barcode.FORMAT_CODABAR
        "PDF417" -> Barcode.FORMAT_PDF417
        "AZTEC" -> Barcode.FORMAT_AZTEC
        "DATA_MATRIX" -> Barcode.FORMAT_DATA_MATRIX
        else -> Barcode.FORMAT_UNKNOWN
    }

    fun icerikTuru(barcode: Barcode, deger: String): IcerikTuru {
        val tur = barcode.valueType
        return when {
            tur == Barcode.TYPE_URL || urlMi(deger) -> IcerikTuru.URL
            tur == Barcode.TYPE_EMAIL -> IcerikTuru.EPOSTA
            tur == Barcode.TYPE_PHONE -> IcerikTuru.TELEFON
            tur == Barcode.TYPE_SMS -> IcerikTuru.SMS
            tur == Barcode.TYPE_GEO -> IcerikTuru.KONUM
            tur == Barcode.TYPE_WIFI || tur == Barcode.TYPE_CONTACT_INFO -> IcerikTuru.WIFI
            tur == Barcode.TYPE_TEXT && deger.isNotBlank() -> IcerikTuru.METIN
            else -> IcerikTuru.BILINMIYEN
        }
    }

    fun urlMi(deger: String): Boolean {
        val temiz = deger.trim()
        if (temiz.isEmpty()) return false
        if (temiz.startsWith("http://", true) || temiz.startsWith("https://", true)) return true
        if (temiz.startsWith("www.", true)) return true
        val sema = runCatching { Uri.parse(temiz).scheme?.lowercase(Locale.US) }.getOrNull()
        return sema == "http" || sema == "https"
    }

    fun baglantiAdresi(deger: String): Uri? {
        val temiz = deger.trim()
        if (temiz.isEmpty()) return null
        val duzeltilmis = if (temiz.startsWith("www.", true)) "http://$temiz" else temiz
        return runCatching { Uri.parse(duzeltilmis) }.getOrNull()
    }

    fun tarihMetni(zamanDamgasi: Long): String =
        SimpleDateFormat("d MMMM yyyy", Locale("tr", "TR")).format(Date(zamanDamgasi))

    fun saatMetni(zamanDamgasi: Long): String =
        SimpleDateFormat("HH:mm", Locale("tr", "TR")).format(Date(zamanDamgasi))
}
