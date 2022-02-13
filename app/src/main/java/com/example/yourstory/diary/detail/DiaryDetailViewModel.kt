package com.example.yourstory.diary.detail

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.database.data.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class DiaryDetailViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var title: String
    private var repository: Repository = Repository(application)
    var deleteState = MutableLiveData(false)
    var selectedItems = MutableLiveData(listOf<Int>())
    lateinit var todayDiaryEntryData : LiveData<List<DiaryEntry>>
    lateinit var todayEmotionalStateEntryData : LiveData<List<EmotionalState>>

    var todayMediaPlayer: MediaPlayer? = null
    var currentAudioTrack = MutableLiveData("")
    var mediaPlayerRunning = MutableLiveData(false)

    fun setDate(date: String){
            title = date
        val year = date.split(".")[2]
        val month = date.split(".")[1]
        val day = date.split(".")[0]

        todayDiaryEntryData = repository.readAllEntriesOfaDate(DateTime(year.toInt(),month.toInt(),day.toInt(),0,0).toString())
        todayEmotionalStateEntryData = repository.readAllEmotionalStatesOfADate(DateTime(year.toInt(),month.toInt(),day.toInt(),0,0).toString())
    }

    fun deleteDiaryEntries(entries: ArrayList<Entry>){
        viewModelScope.launch (Dispatchers.IO) {
            for (entry in entries){
                if(entry is DiaryEntry){
                    repository.deleteDiaryEntry(entry.id)
                }
                if(entry is EmotionalState){
                    repository.deleteEmotionalState(entry.id)
                }
            }
        }
    }
}