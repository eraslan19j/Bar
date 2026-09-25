package com.turkce.barkod.camera

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun KameraOnizleme(
    modifier: Modifier = Modifier,
    analizor: ImageAnalysis.Analyzer?,
    hazirBildir: () -> Unit = {},
    hataBildir: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val yasamSahibi = LocalLifecycleOwner.current

    val onizlemeGorunumu = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    DisposableEffect(yasamSahibi, analizor) {
        val analizYurutucusu = Executors.newSingleThreadExecutor()
        val kapatildi = AtomicBoolean(false)
        var kameraProvider: ProcessCameraProvider? = null
        var analizKullanimi: ImageAnalysis? = null

        val gelecek = ProcessCameraProvider.getInstance(context)
        gelecek.addListener({
            if (kapatildi.get()) return@addListener
            val provider = runCatching { gelecek.get() }.getOrNull()
            if (provider == null) {
                hataBildir("Kamera sağlayıcısı başlatılamadı.")
                return@addListener
            }
            kameraProvider = provider

            val hata = runCatching {
                val onizleme = Preview.Builder().build().also {
                    it.setSurfaceProvider(onizlemeGorunumu.surfaceProvider)
                }
                val secenekler = provider.bindToLifecycle(
                    yasamSahibi,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    onizleme
                )
                val mevcutAnaliz = analizor
                if (mevcutAnaliz != null) {
                    val analiz = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetResolution(Size(1280, 720))
                        .build()
                    analiz.setAnalyzer(analizYurutucusu, mevcutAnaliz)
                    analizKullanimi = analiz
                    secenekler.addUseCase(analiz)
                }
                secenekler
            }

            if (hata.isFailure) {
                hataBildir(hata.exceptionOrNull()?.message ?: "Kamera hatası")
            } else {
                hazirBildir()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            kapatildi.set(true)
            analizKullanimi?.clearAnalyzer()
            runCatching { kameraProvider?.unbindAll() }
            analizYurutucusu.shutdown()
        }
    }

    AndroidView(
        factory = { onizlemeGorunumu },
        modifier = modifier
    )
}
