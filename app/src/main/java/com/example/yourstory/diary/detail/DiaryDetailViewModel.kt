package com.example.yourstory.diary.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import org.joda.time.DateTime

class DiaryDetailViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var title: String
    private var repository: Repository
    lateinit var todayDiaryEntryData : LiveData<List<DiaryEntry>>
    lateinit var todayEmotionalStateEntryData : LiveData<List<EmotionalState>>

    init {
        repository = Repository(application)
    }

    fun setDate(date: String){
            title = date
        var year = date.split(".")[2]
        var month = date.split(".")[1]
        var day = date.split(".")[0]

        todayDiaryEntryData = repository.readAllEntriesOfaDate(DateTime(year.toInt(),month.toInt(),day.toInt(),0,0).toString())
        todayEmotionalStateEntryData = repository.readAllEmotionalStatesOfADate(DateTime(year.toInt(),month.toInt(),day.toInt(),0,0).toString())
    }
}