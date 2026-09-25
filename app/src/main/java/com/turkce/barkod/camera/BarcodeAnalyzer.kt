package com.turkce.barkod.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeAnalyzer(
    private val barkorBulundu: (Barcode) -> Unit
) : ImageAnalysis.Analyzer, Closeable {

    private val tarayici: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(DESTEKLENEN_FORMATLAR)
            .build()
    )

    @Volatile
    private var acik = true

    private val islemSuruyor = AtomicBoolean(false)

    fun duraklat() {
        acik = false
    }

    fun devamEt() {
        acik = true
    }

    fun taramayaHazirMi(): Boolean = acik

    override fun analyze(imageProxy: ImageProxy) {
        if (!acik) {
            imageProxy.close()
            return
        }
        if (!islemSuruyor.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            islemSuruyor.set(false)
            imageProxy.close()
            return
        }
        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )
        tarayici.process(inputImage)
            .addOnSuccessListener { kodlar ->
                val kod = kodlar.firstOrNull { !it.rawValue.isNullOrBlank() }
                if (kod != null && acik) {
                    acik = false
                    barkorBulundu(kod)
                }
            }
            .addOnFailureListener {
                // Görüntü okunamadı; bir sonraki kareye geçilir.
            }
            .addOnCompleteListener {
                islemSuruyor.set(false)
                imageProxy.close()
            }
    }

    override fun close() {
        acik = false
        runCatching { tarayici.close() }
    }

    companion object {
        val DESTEKLENEN_FORMATLAR = intArrayOf(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_PDF417,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_DATA_MATRIX
        )
    }
}
