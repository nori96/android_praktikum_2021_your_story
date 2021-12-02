package com.example.yourstory.database

import androidx.lifecycle.LiveData
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.DiaryEntryDao
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.database.data.EmotionalStateDao

class Repository(private val diaryEntryDao: DiaryEntryDao, private val emotionalStateDao: EmotionalStateDao){

    //Diary Entry functions

    fun readAllEntriesSortedByID(): LiveData<List<DiaryEntry>> {
        return diaryEntryDao.readAllEntriesSortedByID()
    }

    fun readAllEntriesSortedByDate(): LiveData<List<DiaryEntry>>{
        return diaryEntryDao.readAllEntriesSortedByDate()
    }

    fun readAllEntriesByDate(searchDate: String): LiveData<List<DiaryEntry>>{
        return diaryEntryDao.readAllEntriesByDate(searchDate)
    }

    fun getEmotionalStateOfDiaryEntry(diaryEntry: DiaryEntry): LiveData<List<EmotionalState>>{
        return diaryEntryDao.getEmotionalStateOfDiaryEntry(diaryEntry.emotionalStateID)
    }

    suspend fun addDiaryEntry(diaryEntry: DiaryEntry){
        diaryEntryDao.addDiaryEntry(diaryEntry)
    }

    //Emotional State functions

    fun readAllEmotionalStatesSortedByID(): LiveData<List<EmotionalState>>{
        return emotionalStateDao.readAllEmotionalStatesSortedByID()
    }

    fun readAllEmotionalStatesSortedByDate(): LiveData<List<EmotionalState>>{
        return emotionalStateDao.readAllEmotionalStatesSortedByDate()
    }

    suspend fun addEmotionalState(emotionalState: EmotionalState){
        emotionalStateDao.addEmotionalState(emotionalState)
    }
}