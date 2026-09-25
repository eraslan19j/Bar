package com.turkce.barkod.ui.history

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.turkce.barkod.R
import com.turkce.barkod.data.ScanHistoryEntity
import com.turkce.barkod.util.BarcodeUtil
import com.turkce.barkod.util.IcerikTuru

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GecmisEkrani(
    kayitlar: List<ScanHistoryEntity>,
    geriDon: () -> Unit,
    kayitSil: (Long) -> Unit,
    tumunuTemizle: () -> Unit,
    kaydiAc: (ScanHistoryEntity) -> Unit
) {
    var silinecekKayit by remember { mutableStateOf<ScanHistoryEntity?>(null) }
    var tumuSilinecek by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = stringResource(R.string.gecmis_basligi),
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (kayitlar.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.kayit_sayisi, kayitlar.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = geriDon) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.geri)
                    )
                }
            },
            actions = {
                if (kayitlar.isNotEmpty()) {
                    IconButton(onClick = { tumuSilinecek = true }) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = stringResource(R.string.tum_gecmisi_temizle),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground
            )
        )

        if (kayitlar.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.gecmis_bos),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = kayitlar, key = { it.id }) { kayit ->
                    GecmisKaydi(
                        kayit = kayit,
                        sil = { silinecekKayit = kayit },
                        tikla = { kaydiAc(kayit) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { tumuSilinecek = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DeleteOutline,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.tum_gecmisi_temizle),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    silinecekKayit?.let { kayit ->
        OnayDialog(
            baslik = stringResource(R.string.kayit_silinsin_mi),
            onayMetni = stringResource(R.string.sil),
            iptalMetni = stringResource(R.string.hayir),
            onOnay = {
                kayitSil(kayit.id)
                silinecekKayit = null
            },
            onIptal = { silinecekKayit = null }
        )
    }

    if (tumuSilinecek) {
        OnayDialog(
            baslik = stringResource(R.string.tum_gecmis_silinsin_mi),
            onayMetni = stringResource(R.string.evet),
            iptalMetni = stringResource(R.string.hayir),
            onOnay = {
                tumunuTemizle()
                tumuSilinecek = false
            },
            onIptal = { tumuSilinecek = false }
        )
    }
}

@Composable
private fun GecmisKaydi(
    kayit: ScanHistoryEntity,
    sil: () -> Unit,
    tikla: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = tikla),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCode2,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = kayit.value,
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(BarcodeUtil.formatEtiketi(BarcodeUtil.formatKisaAdindan(kayit.format))) +
                        "  •  " +
                        BarcodeUtil.tarihMetni(kayit.scannedAt) + ", " +
                        BarcodeUtil.saatMetni(kayit.scannedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = sil) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = stringResource(R.string.sil),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OnayDialog(
    baslik: String,
    onayMetni: String,
    iptalMetni: String,
    onOnay: () -> Unit,
    onIptal: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onIptal,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = baslik,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            TextButton(onClick = onOnay) {
                Text(
                    text = onayMetni,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onIptal) {
                Text(
                    text = iptalMetni,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
