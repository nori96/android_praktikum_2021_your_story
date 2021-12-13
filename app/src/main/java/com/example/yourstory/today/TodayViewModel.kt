package com.example.yourstory.today

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import org.joda.time.DateTime

class TodayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository
    var todayDiaryEntryData : LiveData<List<DiaryEntry>>
    var todayEmotionalStateEntryData : LiveData<List<EmotionalState>>

    init {
        repository = Repository(application)
        todayDiaryEntryData = repository.readAllEntriesOfaDate(DateTime.now().toString())
        todayEmotionalStateEntryData = repository.readAllEmotionalStatesOfADate(DateTime.now().toString())
    }
}