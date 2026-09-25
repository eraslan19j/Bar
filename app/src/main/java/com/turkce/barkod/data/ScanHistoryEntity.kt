package com.turkce.barkod.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tarama_gecmisi",
    indices = [Index(value = ["scannedAt"])]
)
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val value: String,
    val format: String,
    val scannedAt: Long,
    val contentType: String
)
