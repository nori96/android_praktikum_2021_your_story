package com.example.yourstory.today

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TodayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository
    var todayViewData : LiveData<List<DiaryEntry>>

    init {
        repository = Repository(application)
        todayViewData = repository.readAllEntriesSortedByDate()
    }
}