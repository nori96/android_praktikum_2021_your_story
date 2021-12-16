package com.example.yourstory.reports.createReports

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.database.data.ReportEntry
import com.example.yourstory.diary.DiaryListModel
import com.example.yourstory.utils.DateEpochConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


class CreateReportViewModel(application: Application) : AndroidViewModel(application) {
    var firstSelectedDate = MutableLiveData(0L)
    var lastSelectedDate = MutableLiveData(0L)

    var joySelected = MutableLiveData(false)
    var angerSelected = MutableLiveData(false)
    var surpriseSelected = MutableLiveData(false)
    var sadnessSelected = MutableLiveData(false)
    var disgustSelected = MutableLiveData(false)
    var fearSelected = MutableLiveData(false)

    var joyAverage = MutableLiveData(0f)
    var angerAverage = MutableLiveData(0f)
    var surpriseAverage = MutableLiveData(0f)
    var sadnessAverage = MutableLiveData(0f)
    var disgustAverage = MutableLiveData(0f)
    var fearAverage = MutableLiveData(0f)

    private val repository = Repository(application)
    var emotionalStatesObserver : LiveData<List<EmotionalState>> =
        repository.readAllEmotionalStatesBetweenDates(firstSelectedDate.value!!, lastSelectedDate.value!!)
    val viewExposedStates = MutableLiveData<List<EmotionalState>>()

    // execute this function when something in pie chart has changed
    fun setObservableArea() {
        emotionalStatesObserver = repository.readAllEmotionalStatesBetweenDates(firstSelectedDate.value!!, lastSelectedDate.value!!)
        emotionalStatesObserver.observeForever { newObservableStates ->
            viewExposedStates.value = newObservableStates
        }
    }
    fun insertCurrentReport() {
        viewModelScope.launch (Dispatchers.IO) {
            Log.i("asdf",firstSelectedDate.value!!.toString())
            val fmt: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
            repository.addReportEntry(ReportEntry(0,
                DateEpochConverter.getCurrentEpoch(),
                fmt.print(DateEpochConverter.convertEpochToDateTime(firstSelectedDate.value!!))+
                        " - " +
                        fmt.print(DateEpochConverter.convertEpochToDateTime(lastSelectedDate.value!!)),
                calculateEmotionalAverage(),
                getCurrentSelectionJoy(),
                getCurrentSelectionAnger(),
                getCurrentSelectionSurprise(),
                getCurrentSelectionSadness(),
                getCurrentSelectionDisgust(),
                getCurrentSelectionFear())
            )
        }
    }

    fun calculateEmotionalAverage() : Float {
        var counter = 0
        var emotionality = 0f
        if (joyAverage.value!! > 0)
            counter +=1
            emotionality += joyAverage.value!!
        if (angerAverage.value!! > 0)
            counter +=1
            emotionality += angerAverage.value!!
        if (surpriseAverage.value!! > 0)
            counter +=1
            emotionality += surpriseAverage.value!!
        if (sadnessAverage.value!! > 0)
            counter +=1
            emotionality += sadnessAverage.value!!
        if (disgustAverage.value!! > 0)
            counter +=1
            emotionality += disgustAverage.value!!
        if (fearAverage.value!! > 0)
            counter +=1
            emotionality += fearAverage.value!!
        return emotionality / counter
    }

    private fun getCurrentSelectionJoy(): Float {
        return if (joySelected.value!!) joyAverage.value!! else 0f
    }
    private fun getCurrentSelectionAnger(): Float {
        return if (angerSelected.value!!) angerAverage.value!! else 0f
    }
    private fun getCurrentSelectionSurprise(): Float {
        return if (surpriseSelected.value!!) surpriseAverage.value!! else 0f
    }
    private fun getCurrentSelectionSadness(): Float {
        return if (sadnessSelected.value!!) sadnessAverage.value!! else 0f
    }
    private fun getCurrentSelectionDisgust(): Float {
        return if (disgustSelected.value!!) disgustAverage.value!! else 0f
    }
    private fun getCurrentSelectionFear(): Float {
        return if (fearSelected.value!!) fearAverage.value!! else 0f
    }
}