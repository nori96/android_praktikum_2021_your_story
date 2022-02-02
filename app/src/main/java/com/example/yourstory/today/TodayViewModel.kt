package com.example.yourstory.today

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.database.data.Entry
import com.example.yourstory.utils.DateEpochConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class TodayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository
    var deleteState = MutableLiveData(false)
    var selectedItems = MutableLiveData(listOf<Int>())
    var todayDiaryEntryData : LiveData<List<DiaryEntry>>
    var todayEmotionalStateEntryData : LiveData<List<EmotionalState>>

    init {
        repository = Repository(application)
        todayDiaryEntryData = repository.readAllEntriesOfaDate(DateTime.now().toString())
        todayEmotionalStateEntryData = repository.readAllEmotionalStatesOfADate(DateTime.now().toString())
    }

    fun deleteDiaryEntries(entries: ArrayList<Entry>){
        viewModelScope.launch (Dispatchers.IO) {
            for (entry in entries){
                if(entry is DiaryEntry){
                    repository.diaryEntryDao.deleteDiaryEntryById(entry.id)
                }
                if(entry is EmotionalState){
                    System.out.println(entry.id)
                    repository.emotionalStateDao.deleteEmotionalStateByID(entry.id)
                }
            }
        }
    }
}