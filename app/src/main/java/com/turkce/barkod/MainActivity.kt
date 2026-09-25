package com.turkce.barkod

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.turkce.barkod.camera.BarcodeAnalyzer
import com.turkce.barkod.ui.AppViewModel
import com.turkce.barkod.ui.IzinEkrani
import com.turkce.barkod.ui.history.GecmisEkrani
import com.turkce.barkod.ui.result.SonucEkrani
import com.turkce.barkod.ui.scan.TaramaEkrani
import com.turkce.barkod.ui.settings.AyarlarEkrani
import com.turkce.barkod.ui.theme.BarkodTheme
import com.turkce.barkod.util.BarcodeUtil
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AppViewModel = viewModel()
            val ayarlar by viewModel.ayarlar.collectAsStateWithLifecycle()
            BarkodTheme(koyuTema = ayarlar.koyuTema) {
                BarkodUygulamasi(viewModel)
            }
        }
    }
}

private const val ROTA_TARAMA = "tarama"
private const val ROTA_SONUC = "sonuc"
private const val ROTA_GECMIS = "gecmis"
private const val ROTA_AYARLAR = "ayarlar"

@Composable
private fun BarkodUygulamasi(viewModel: AppViewModel) {
    val context = LocalContext.current
    val kapsam = rememberCoroutineScope()
    val navController = rememberNavController()
    val snackbarDurumu = remember { SnackbarHostState() }

    val ayarlar by viewModel.ayarlar.collectAsStateWithLifecycle()
    val sonuc by viewModel.sonuc.collectAsStateWithLifecycle()
    val sonucVar by viewModel.sonucVar.collectAsStateWithLifecycle()
    val gecmis by viewModel.gecmis.collectAsStateWithLifecycle()

    var izinVerildi by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var kaliciReddedildi by remember { mutableStateOf(false) }
    var kameraHatasi by remember { mutableStateOf<String?>(null) }

    val analizor = remember {
        BarcodeAnalyzer { barkod ->
            kapsam.launch { viewModel.barkodAlgilandi(barkod) }
        }
    }

    DisposableEffect(Unit) {
        onDispose { analizor.close() }
    }

    val izinIsteyici = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { verildi ->
        izinVerildi = verildi
        kaliciReddedildi = !verildi && !ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            Manifest.permission.CAMERA
        )
    }

    // Uygulama arka plandan dönünce izin durumunu tazele.
    val yasamSahibi = LocalLifecycleOwner.current
    DisposableEffect(yasamSahibi) {
        val gozlemci = LifecycleEventObserver { _, olay ->
            if (olay == Lifecycle.Event.ON_RESUME) {
                izinVerildi = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (izinVerildi) kaliciReddedildi = false
            }
        }
        yasamSahibi.lifecycle.addObserver(gozlemci)
        onDispose { yasamSahibi.lifecycle.removeObserver(gozlemci) }
    }

    LaunchedEffect(Unit) {
        if (!izinVerildi) {
            izinIsteyici.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(sonucVar) {
        if (sonucVar) {
            navController.navigate(ROTA_SONUC) { launchSingleTop = true }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.mesajlar.collect { kaynak ->
            snackbarDurumu.showSnackbar(context.getString(kaynak))
        }
    }

    val surum = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarDurumu) }
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = ROTA_TARAMA
        ) {
            composable(ROTA_TARAMA) {
                if (izinVerildi) {
                    TaramaEkrani(
                        analizor = analizor,
                        taramayaDevam = !sonucVar,
                        kameraHatasi = kameraHatasi,
                        kameraHatasiniBildir = { kameraHatasi = it },
                        gecmiseGit = { navController.navigate(ROTA_GECMIS) },
                        ayarlaraGit = { navController.navigate(ROTA_AYARLAR) }
                    )
                } else {
                    IzinEkrani(
                        kaliciReddedildi = kaliciReddedildi,
                        izinIste = {
                            if (kaliciReddedildi) {
                                ayarlariAc(context)
                            } else {
                                izinIsteyici.launch(Manifest.permission.CAMERA)
                            }
                        },
                        ayarlariAc = { ayarlariAc(context) }
                    )
                }
            }

            composable(ROTA_SONUC) {
                val mevcut = sonuc
                if (mevcut == null) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                } else {
                    SonucEkrani(
                        sonuc = mevcut,
                        kopyala = { deger -> panoyaKopyala(context, deger, viewModel) },
                        paylas = { deger -> paylasEt(context, deger, viewModel) },
                        baglantiyiAc = { deger -> baglantiyiAcEt(context, deger, viewModel) },
                        gecmiseKaydet = viewModel::sonucuGecmiseKaydet,
                        yeniTarama = {
                            viewModel.yeniTaramaBaslat()
                            navController.popBackStack(ROTA_TARAMA, inclusive = false)
                        }
                    )
                }
            }

            composable(ROTA_GECMIS) {
                GecmisEkrani(
                    kayitlar = gecmis,
                    geriDon = { navController.popBackStack() },
                    kayitSil = viewModel::kayitSil,
                    tumunuTemizle = viewModel::tumGecmisiTemizle,
                    kaydiAc = { kayit ->
                        viewModel.gecmisKaydiniSonucYap(kayit)
                        navController.navigate(ROTA_SONUC) { launchSingleTop = true }
                    }
                )
            }

            composable(ROTA_AYARLAR) {
                AyarlarEkrani(
                    ayarlar = ayarlar,
                    surum = surum,
                    geriDon = { navController.popBackStack() },
                    sesDegisti = viewModel::sesAyariniDegistir,
                    titresimDegisti = viewModel::titresimAyariniDegistir,
                    koyuTemaDegisti = viewModel::koyuTemayiDegistir,
                    gecmiseGit = { navController.navigate(ROTA_GECMIS) },
                    tumGecmisiTemizle = viewModel::tumGecmisiTemizle
                )
            }
        }
    }
}

private fun ayarlariAc(context: Context) {
    runCatching {
        context.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

private fun panoyaKopyala(context: Context, deger: String, viewModel: AppViewModel) {
    val sonuc = runCatching {
        val pano = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        pano?.setPrimaryClip(ClipData.newPlainText("Barkod", deger))
    }.isSuccess
    if (sonuc) viewModel.kopyalandi()
}

private fun paylasEt(context: Context, deger: String, viewModel: AppViewModel) {
    val calisti = runCatching {
        val niyet = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, deger)
        }
        context.startActivity(
            Intent.createChooser(niyet, context.getString(R.string.paylas))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }.isSuccess
    if (!calisti) viewModel.mesajVer(R.string.paylasma_hatasi)
}

private fun baglantiyiAcEt(context: Context, deger: String, viewModel: AppViewModel) {
    val adres = BarcodeUtil.baglantiAdresi(deger)
    if (adres == null) {
        viewModel.mesajVer(R.string.baglanti_acilamadi)
        return
    }
    val calisti = runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, adres).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }.isSuccess
    if (!calisti) viewModel.mesajVer(R.string.baglanti_acilamadi)
}
