package com.turkce.barkod.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turkce.barkod.R
import com.turkce.barkod.settings.AppSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyarlarEkrani(
    ayarlar: AppSettings,
    surum: String,
    geriDon: () -> Unit,
    sesDegisti: (Boolean) -> Unit,
    titresimDegisti: (Boolean) -> Unit,
    koyuTemaDegisti: (Boolean) -> Unit,
    gecmiseGit: () -> Unit,
    tumGecmisiTemizle: () -> Unit
) {
    var silOnayi by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.ayarlar_basligi),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = geriDon) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.geri)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 4.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                AyarKarti(baslik = null) {
                    AyarSatiri(
                        ikon = Icons.Filled.VolumeUp,
                        baslik = stringResource(R.string.ayar_ses),
                        aciklama = stringResource(R.string.ayar_ses_aciklama),
                        deger = ayarlar.ses,
                        degerDegisti = sesDegisti
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    AyarSatiri(
                        ikon = Icons.Filled.Vibration,
                        baslik = stringResource(R.string.ayar_titresim),
                        aciklama = stringResource(R.string.ayar_titresim_aciklama),
                        deger = ayarlar.titresim,
                        degerDegisti = titresimDegisti
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    AyarSatiri(
                        ikon = Icons.Filled.DarkMode,
                        baslik = stringResource(R.string.ayar_koyu_tema),
                        aciklama = stringResource(R.string.ayar_koyu_tema_aciklama),
                        deger = ayarlar.koyuTema,
                        degerDegisti = koyuTemaDegisti
                    )
                }
            }

            item {
                AyarKarti(baslik = null) {
                    AyarSatiri(
                        ikon = Icons.Filled.History,
                        baslik = stringResource(R.string.ayar_tarama_gecmisi),
                        aciklama = stringResource(R.string.ayar_tarama_gecmisi_aciklama),
                        tikla = gecmiseGit
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    AyarSatiri(
                        ikon = Icons.Filled.DeleteOutline,
                        baslik = stringResource(R.string.tum_gecmisi_temizle),
                        aciklama = null,
                        tikla = { silOnayi = true },
                        ikonRengi = MaterialTheme.colorScheme.error,
                        yaziRengi = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                AyarKarti(baslik = stringResource(R.string.hakkinda_baslik)) {
                    AyarSatiri(
                        ikon = Icons.Filled.Info,
                        baslik = stringResource(R.string.hakkinda_surum, surum),
                        aciklama = stringResource(R.string.hakkinda_gizlilik)
                    )
                }
            }
        }
    }

    if (silOnayi) {
        AlertDialog(
            onDismissRequest = { silOnayi = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = stringResource(R.string.tum_gecmis_silinsin_mi),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        tumGecmisiTemizle()
                        silOnayi = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.evet),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { silOnayi = false }) {
                    Text(
                        text = stringResource(R.string.hayir),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}

@Composable
private fun AyarKarti(
    baslik: String?,
    icerik: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (baslik != null) {
            Text(
                text = baslik,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                icerik()
            }
        }
    }
}

@Composable
private fun AyarSatiri(
    ikon: ImageVector,
    baslik: String,
    aciklama: String?,
    deger: Boolean? = null,
    degerDegisti: ((Boolean) -> Unit)? = null,
    tikla: (() -> Unit)? = null,
    ikonRengi: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    yaziRengi: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (tikla != null) {
                    Modifier.clickable(onClick = tikla)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    ikonRengi.copy(alpha = 0.14f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ikon,
                contentDescription = null,
                tint = ikonRengi,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            Text(
                text = baslik,
                style = MaterialTheme.typography.bodyLarge,
                color = yaziRengi
            )
            if (aciklama != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = aciklama,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (deger != null && degerDegisti != null) {
            Switch(
                checked = deger,
                onCheckedChange = degerDegisti
            )
        }
    }
}
