package com.example.pico_botella.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pico_botella.model.Challenge

@Database(entities = [Challenge::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pico_botella_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}