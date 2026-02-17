package com.example.storageapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StorageItem::class], version = 1)
abstract class StorageDatabase : RoomDatabase() {
    abstract fun storageItemDao(): StorageItemDao

    companion object {
        @Volatile
        private var INSTANCE: StorageDatabase? = null

        fun getDatabase(context: Context): StorageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StorageDatabase::class.java,
                    "storage_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
