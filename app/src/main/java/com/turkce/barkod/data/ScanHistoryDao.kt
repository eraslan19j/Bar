package com.turkce.barkod.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Query("SELECT * FROM tarama_gecmisi ORDER BY scannedAt DESC")
    fun observeAll(): Flow<List<ScanHistoryEntity>>

    @Insert
    suspend fun insert(entity: ScanHistoryEntity): Long

    @Query("DELETE FROM tarama_gecmisi WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM tarama_gecmisi")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM tarama_gecmisi")
    fun observeCount(): Flow<Int>
}
