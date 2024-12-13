package com.example.soundboard

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Soundbank::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun soundbankDao(): SoundbankDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "soundboard_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}