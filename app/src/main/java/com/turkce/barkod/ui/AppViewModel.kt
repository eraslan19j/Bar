package com.turkce.barkod.ui

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.turkce.barkod.data.AppDatabase
import com.turkce.barkod.data.ScanHistoryEntity
import com.turkce.barkod.data.ScanRepository
import com.turkce.barkod.settings.AppSettings
import com.turkce.barkod.settings.FeedbackHelper
import com.turkce.barkod.settings.SettingsRepository
import com.turkce.barkod.util.BarcodeUtil
import com.turkce.barkod.util.IcerikTuru
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class BarcodeSonucu(
    val deger: String,
    val format: Int,
    val icerikTuru: IcerikTuru,
    val okumaZamani: Long,
    val gecmiseKaydedildi: Boolean = false
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val scanRepository = ScanRepository(AppDatabase.get(application).scanHistoryDao())
    private val settingsRepository = SettingsRepository(application)
    private val geriBildirim = FeedbackHelper(application)

    val ayarlar: StateFlow<AppSettings> = settingsRepository.ayarlar

    val gecmis: StateFlow<List<ScanHistoryEntity>> = scanRepository.taramaGecmisi
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _sonuc = MutableStateFlow<BarcodeSonucu?>(null)
    val sonuc: StateFlow<BarcodeSonucu?> = _sonuc.asStateFlow()

    private val _sonucVar = MutableStateFlow(false)
    val sonucVar: StateFlow<Boolean> = _sonucVar.asStateFlow()

    private val mesajKanal = Channel<Int>(Channel.BUFFERED)
    val mesajlar = mesajKanal.receiveAsFlow()

    private var sonOkunanDeger: String? = null
    private var sonOkunanZaman: Long = 0L

    fun barkodAlgilandi(barcode: Barcode) {
        val deger = barcode.rawValue ?: return
        if (deger.isBlank()) return
        val simdi = System.currentTimeMillis()
        if (deger == sonOkunanDeger && simdi - sonOkunanZaman < TEKRAR_ALGILAMA_SURESI_MS) return
        sonOkunanDeger = deger
        sonOkunanZaman = simdi

        val ayarlar = ayarlar.value
        if (ayarlar.ses) geriBildirim.basariSesi()
        if (ayarlar.titresim) geriBildirim.titret()

        _sonuc.value = BarcodeSonucu(
            deger = deger,
            format = barcode.format,
            icerikTuru = BarcodeUtil.icerikTuru(barcode, deger),
            okumaZamani = simdi
        )
        _sonucVar.value = true
    }

    fun gecmisKaydiniSonucYap(kayit: ScanHistoryEntity) {
        _sonuc.value = BarcodeSonucu(
            deger = kayit.value,
            format = BarcodeUtil.formatKisaAdindan(kayit.format),
            icerikTuru = IcerikTuru.anahtardan(kayit.contentType),
            okumaZamani = kayit.scannedAt,
            gecmiseKaydedildi = true
        )
    }

    fun mesajVer(@StringRes kaynak: Int) {
        viewModelScope.launch { mesajKanal.send(kaynak) }
    }

    fun kopyalandi() = mesajVer(com.turkce.barkod.R.string.kopyalandi)

    fun yeniTaramaBaslat() {
        _sonucVar.value = false
        _sonuc.value = null
        sonOkunanDeger = null
        sonOkunanZaman = 0L
    }

    fun sonucuGecmiseKaydet() {
        val mevcut = _sonuc.value ?: return
        if (mevcut.gecmiseKaydedildi) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                scanRepository.kaydet(
                    value = mevcut.deger,
                    format = BarcodeUtil.formatKisaAdi(mevcut.format),
                    scannedAt = mevcut.okumaZamani,
                    contentType = mevcut.icerikTuru.anahtar
                )
            }
            _sonuc.update { it?.copy(gecmiseKaydedildi = true) }
            mesajVer(com.turkce.barkod.R.string.gecmise_kaydedildi)
        }
    }

    fun kayitSil(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { scanRepository.sil(id) }
            mesajVer(com.turkce.barkod.R.string.kayit_silindi)
        }
    }

    fun tumGecmisiTemizle() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { scanRepository.tumunuTemizle() }
            mesajVer(com.turkce.barkod.R.string.gecmis_temizlendi)
        }
    }

    fun sesAyariniDegistir(deger: Boolean) = settingsRepository.sesAyariniDegistir(deger)

    fun titresimAyariniDegistir(deger: Boolean) =
        settingsRepository.titresimAyariniDegistir(deger)

    fun koyuTemayiDegistir(deger: Boolean) = settingsRepository.koyuTemayiDegistir(deger)

    override fun onCleared() {
        geriBildirim.kapat()
        super.onCleared()
    }

    private companion object {
        const val TEKRAR_ALGILAMA_SURESI_MS = 2_000L
    }
}
