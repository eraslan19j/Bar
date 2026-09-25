package com.turkce.barkod.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ScanHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var ornek: AppDatabase? = null

        fun get(context: Context): AppDatabase {
            return ornek ?: synchronized(this) {
                ornek ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "barkod_gecmisi.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { ornek = it }
            }
        }
    }
}
