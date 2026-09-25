package com.turkce.barkod.ui.scan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkce.barkod.R
import com.turkce.barkod.camera.BarcodeAnalyzer
import com.turkce.barkod.camera.KameraOnizleme

@Composable
fun TaramaEkrani(
    analizor: BarcodeAnalyzer,
    taramayaDevam: Boolean,
    kameraHatasi: String?,
    kameraHatasiniBildir: (String) -> Unit,
    gecmiseGit: () -> Unit,
    ayarlaraGit: () -> Unit
) {
    var kameraHazir by remember { mutableStateOf(false) }

    LaunchedEffect(taramayaDevam) {
        if (taramayaDevam) analizor.devamEt() else analizor.duraklat()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        KameraOnizleme(
            modifier = Modifier.fillMaxSize(),
            analizor = analizor,
            hazirBildir = { kameraHazir = true },
            hataBildir = { kameraHatasiniBildir(it) }
        )

        // Karartma katmanı
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f))
        )

        Column(modifier = Modifier.fillMaxSize()) {

            UstBaslik()

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TaramaCercevesi(aktif = kameraHazir)
            }

            AltBolum(
                kameraHatasi = kameraHatasi,
                gecmiseGit = gecmiseGit,
                ayarlaraGit = ayarlaraGit
            )
        }
    }
}

@Composable
private fun UstBaslik() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 10.dp, bottom = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.tarama_basligi),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}

@Composable
private fun TaramaCercevesi(aktif: Boolean) {
    val ekranGenisligi = LocalConfiguration.current.screenWidthDp
    val cerceveGenisligi = (ekranGenisligi * 0.78f).coerceIn(200.dp, 340.dp)

    val birincilRenk = MaterialTheme.colorScheme.primary
    val gecis = rememberInfiniteTransition(label = "taramaCizgisi")
    val konum by gecis.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cizgiKonumu"
    )
    val nefes by gecis.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cerceveNefesi"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .width(cerceveGenisligi)
                .aspectRatio(1f / 0.68f)
        ) {
            // Çerçeve köşeleri
            Canvas(modifier = Modifier.fillMaxSize()) {
                val kalinlik = 4.dp.toPx()
                val kol = 30.dp.toPx()
                val yariCap = 18.dp.toPx()
                val alfa = if (aktif) nefes else 0.3f
                val renk = birincilRenk.copy(alpha = alfa)

                val sol = 0f
                val ust = 0f
                val sag = size.width
                val alt = size.height

                drawLine(renk, Offset(sol, ust + yariCap), Offset(sol, alt - yariCap), kalinlik, StrokeCap.Round)
                drawLine(renk, Offset(sol, ust), Offset(sol + kol, ust), kalinlik, StrokeCap.Round)
                drawLine(renk, Offset(sol, ust), Offset(sol, ust + kol), kalinlik, StrokeCap.Round)

                drawLine(renk, Offset(sag, ust + yariCap), Offset(sag, alt - yariCap), kalinlik, StrokeCap.Round)
                drawLine(renk, Offset(sag, ust), Offset(sag - kol, ust), kalinlik, StrokeCap.Round)
                drawLine(renk, Offset(sag, ust), Offset(sag, ust + kol), kalinlik, StrokeCap.Round)

                drawLine(renk, Offset(sol, alt - yariCap), Offset(sol, alt), kalinlik, StrokeCap.Round)
                drawLine(renk, Offset(sol, alt), Offset(sol + kol, alt), kalinlik, StrokeCap.Round)

                drawLine(renk, Offset(sag, alt - yariCap), Offset(sag, alt), kalinlik, StrokeCap.Round)
                drawLine(renk, Offset(sag, alt), Offset(sag - kol, alt), kalinlik, StrokeCap.Round)
                drawLine(renk, Offset(sag - kol, alt), Offset(sag, alt), kalinlik, StrokeCap.Round)
            }

            // Dış ince çerçeve
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
            ) {
                drawRoundRect(
                    color = birincilRenk.copy(alpha = 0.18f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Hareketli tarama çizgisi
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (!aktif) return@Canvas
                val cizgiYuksekligi = 46.dp.toPx()
                val y = size.height * konum
                val ustY = (y - cizgiYuksekligi / 2f).coerceIn(0f, size.height - cizgiYuksekligi)
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            birincilRenk.copy(alpha = 0.35f),
                            birincilRenk
                        ),
                        startY = ustY,
                        endY = ustY + cizgiYuksekligi
                    ),
                    topLeft = Offset(0f, ustY),
                    size = Size(size.width, cizgiYuksekligi)
                )
                drawRect(
                    color = Color.White.copy(alpha = 0.9f),
                    topLeft = Offset(0f, ustY + cizgiYuksekligi - 1.5f),
                    size = Size(size.width, 1.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(
                if (aktif) R.string.barkod_araniyor else R.string.kamera_hazirlaniyor
            ),
            style = MaterialTheme.typography.labelLarge,
            color = birincilRenk
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.tarama_yonlendirme),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AltBolum(
    kameraHatasi: String?,
    gecmiseGit: () -> Unit,
    ayarlaraGit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (kameraHatasi != null) {
            Text(
                text = stringResource(R.string.kamera_hata, kameraHatasi),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = gecmiseGit,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.gecmis),
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 15.sp
                )
            }

            OutlinedButton(
                onClick = ayarlaraGit,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.ayarlar),
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 15.sp
                )
            }
        }
    }
}
