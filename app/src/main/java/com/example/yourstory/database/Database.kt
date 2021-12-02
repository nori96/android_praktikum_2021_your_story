package com.example.yourstory.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.DiaryEntryDao
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.database.data.EmotionalStateDao
import java.security.AccessControlContext

@Database(entities = [DiaryEntry::class, EmotionalState::class], version = 1, exportSchema = false)
abstract class Database: RoomDatabase() {

    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun emotionalStateDao(): EmotionalStateDao

    companion object{
        @Volatile
        private var INSTANCE: com.example.yourstory.database.Database? = null

        fun getDatabase(context: Context): com.example.yourstory.database.Database{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    com.example.yourstory.database.Database::class.java,
                    "database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}