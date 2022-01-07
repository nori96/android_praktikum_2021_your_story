package com.example.yourstory.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.yourstory.database.data.*
import java.io.File
import java.security.AccessControlContext

@Database(entities = [DiaryEntry::class, EmotionalState::class, ReportEntry::class], version = 1, exportSchema = false)
abstract class Database: RoomDatabase() {

    //All Daos should be defined here
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun emotionalStateDao(): EmotionalStateDao
    abstract fun reportEntryDao(): ReportEntryDao

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

        fun prepopulateDatabase(context: Context,database: File){
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return
            }
            synchronized(this) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    com.example.yourstory.database.Database::class.java,
                    "database",
                )
                    .createFromFile(database)
                    .build()
            }
        }
    }
}