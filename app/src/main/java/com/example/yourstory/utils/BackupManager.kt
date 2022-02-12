package com.example.yourstory.utils

import android.content.Context
import com.example.yourstory.database.Database

class BackupManager(var context: Context){

    lateinit var dbPath: String

    init {
    }

    fun initDB(){
        var backUpDatabase = context.getDatabasePath("database_backup")
        if(!backUpDatabase.exists()){
            Database.getDatabase(context)
        }else{
            Database.prepopulateDatabase(context,backUpDatabase)
            backUpDatabase.delete()
        }
    }
}