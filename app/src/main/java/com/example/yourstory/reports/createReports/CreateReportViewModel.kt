package com.example.yourstory.reports.createReports

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.diary.DiaryListModel

class CreateReportViewModel(application: Application) : AndroidViewModel(application) {
    var firstSelectedDate = MutableLiveData(0L)
    var lastSelectedDate = MutableLiveData(0L)
    var joySelected = MutableLiveData(false)
    var angerSelected = MutableLiveData(false)
    var surpriseSelected = MutableLiveData(false)
    var sadnessSelected = MutableLiveData(false)
    var disgustSelected = MutableLiveData(false)
    var fearSelected = MutableLiveData(false)
    private val repository = Repository(application)
    var emotionalStatesObserver : LiveData<List<EmotionalState>> =
        repository.readAllEmotionalStatesBetweenDates(firstSelectedDate.value!!, lastSelectedDate.value!!)
    val viewExposedStates = MutableLiveData<List<EmotionalState>>()

    /*init {
        emotionalStatesObserver.observeForever { newObservableStates ->
            viewExposedStates.value = newObservableStates
        }
    }*/
    // execute this function when something in pie chart has changed
    fun setObservableArea() {
        emotionalStatesObserver = repository.readAllEmotionalStatesBetweenDates(firstSelectedDate.value!!, lastSelectedDate.value!!)
        emotionalStatesObserver.observeForever { newObservableStates ->
            viewExposedStates.value = newObservableStates
        }
    }
}