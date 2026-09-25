package com.turkce.barkod.data

import kotlinx.coroutines.flow.Flow

class ScanRepository(private val dao: ScanHistoryDao) {

    val taramaGecmisi: Flow<List<ScanHistoryEntity>> = dao.observeAll()

    suspend fun kaydet(
        value: String,
        format: String,
        scannedAt: Long,
        contentType: String
    ): Long = dao.insert(
        ScanHistoryEntity(
            value = value,
            format = format,
            scannedAt = scannedAt,
            contentType = contentType
        )
    )

    suspend fun sil(id: Long) = dao.deleteById(id)

    suspend fun tumunuTemizle() = dao.deleteAll()
}
