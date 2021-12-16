package com.example.yourstory.diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.diary.detail.Date
import com.example.yourstory.utils.DateEpochConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

//TODO: Limit Month Spinner to Existing Dates in Line 50
class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    var currentMonth = DateTime.now().monthOfYear().get()
    var currentYear = DateTime.now().year.toString()

    //SpinnerData
    var months_items = MutableLiveData<ArrayList<String>>(arrayListOf())
    var years_items =  MutableLiveData<ArrayList<String>>(arrayListOf())

    var yearOfOldestDbEntry = DateTime.now().year().get()

    private val repository: Repository = Repository(application)
    var diaryEntriesAsListModel = MutableLiveData<ArrayList<DiaryListModel>>()
    lateinit var diaryEntries : LiveData<List<DiaryEntry>>
    lateinit var emotionalStateEntries: LiveData<List<EmotionalState>>

    var emotionalStateEntriesCopy = listOf<EmotionalState>()
    var diaryEntriesCopy = listOf<DiaryEntry>()

    init {

        fetchFilteredData()

        //init Spinner Data
        for(idx in 1..12){
            months_items.value!!.add(DateEpochConverter.monthIntToString(application,idx))
        }

        //Sets the years of the Spinner to the oldest Entry
        for(idx in 0.. 5){
            var prevYear = Calendar.getInstance()
            prevYear.add(Calendar.YEAR,idx * -1)
            years_items.value!!.add(prevYear.get(Calendar.YEAR).toString())
        }
    }

    /*
    fun calculateOldestEntry() {
        val dateOfLastEmotionalState = repository.readoldestEmotionalStateDate()
        val dateOfLastEntry = repository.readOldestEntry()
        if(dateOfLastEmotionalState == null && dateOfLastEntry == null){
            yearOfOldestDbEntry = DateTime.now().year
            return
        }
        if(dateOfLastEmotionalState == null){
            yearOfOldestDbEntry = DateEpochConverter.convertEpochToDateTime(dateOfLastEntry.date).year
            return
        }
        if(dateOfLastEntry == null){
            yearOfOldestDbEntry = DateEpochConverter.convertEpochToDateTime(dateOfLastEmotionalState.date).year
            return
        }
        if(dateOfLastEmotionalState.date < dateOfLastEntry.date){
            yearOfOldestDbEntry = DateEpochConverter.convertEpochToDateTime(dateOfLastEmotionalState.date).year
        }else{
            yearOfOldestDbEntry = DateEpochConverter.convertEpochToDateTime(dateOfLastEntry.date).year
        }
    }
     */

    private fun convertDiaryEntriesToListModel() {
        var dayToEntriesMap = HashMap<String,ArrayList<DiaryEntry>>()
        var dayToEmoStateMap = HashMap<String,ArrayList<EmotionalState>>()

        var newEntriesList = arrayListOf<DiaryListModel>()

        //Fill Map for Entries
        for (diaryEntry in diaryEntriesCopy){
            val day = DateEpochConverter.convertEpochToDateTime(diaryEntry.date).toString().split("T")[0]
            if(dayToEntriesMap.containsKey(day)){
                var entryList = dayToEntriesMap[day]
                entryList!!.add(diaryEntry)
            }else{
                dayToEntriesMap.put(day, arrayListOf(diaryEntry))
            }
        }

        //Fill Map for Emotional States
        for(emotionalState in emotionalStateEntriesCopy){
            val day = DateEpochConverter.convertEpochToDateTime(emotionalState.date).toString().split("T")[0]
            if(dayToEmoStateMap.containsKey(day)){
                var emoStateList = dayToEmoStateMap[day]
                emoStateList!!.add(emotionalState)
            }else{
                dayToEmoStateMap.put(day, arrayListOf(emotionalState))
            }
        }


        for((day,entries) in dayToEntriesMap) {
            //No Emotional State Exists
            if (!dayToEmoStateMap.containsKey(day)) {
                newEntriesList.add(
                    DiaryListModel(
                        Date(
                            day.split("-")[2],
                            day.split("-")[1],
                            day.split("-")[0]
                        ), entries.size, 0F, 0F, 0F, 0F, 0F, 0F
                    )
                )
            } else {
                //Emotional State exist
                var emotionalStates = dayToEmoStateMap.get(day)
                var emotionalStatesSize = emotionalStates!!.size
                var joyAverage = 0F
                var angerAverage = 0F
                var surpriseAverage = 0F
                var sadnessAverage = 0F
                var disgustAverage = 0F
                var fearAverage = 0F
                for (emoElement in emotionalStates) {
                    joyAverage += emoElement.joy
                    angerAverage += emoElement.anger
                    surpriseAverage += emoElement.surprise
                    sadnessAverage += emoElement.sadness
                    disgustAverage += emoElement.disgust
                    fearAverage += emoElement.fear
                }
                //TODO: Einträge Spalten und Average rausnehmen 1 EmotionalState ohne Entrys Fall behandeln
                newEntriesList.add(
                    DiaryListModel(
                        Date(
                            day.split("-")[2],
                            day.split("-")[1],
                            day.split("-")[0]
                        ), entries.size + emotionalStatesSize,
                        (joyAverage / emotionalStatesSize),
                        (angerAverage / emotionalStatesSize),
                        (surpriseAverage / emotionalStatesSize),
                        (sadnessAverage / emotionalStatesSize),
                        (disgustAverage / emotionalStatesSize),
                        (fearAverage / emotionalStatesSize)
                    )
                )
            }
        }
        diaryEntriesAsListModel.value = newEntriesList
    }

    fun fetchFilteredData() {
        var filterDate = DateTime(currentYear.toInt(),currentMonth,1,1,1,1,1)
        diaryEntries = repository.readAllEntriesOfaMonth(filterDate.toString())
        emotionalStateEntries = repository.readEmotionalStatesOfAMonth(filterDate.toString())

        diaryEntries.observeForever { newDiaryEntries ->
            diaryEntriesCopy = newDiaryEntries
            convertDiaryEntriesToListModel()
        }

        emotionalStateEntries.observeForever{
                newEmotionalStates -> emotionalStateEntriesCopy = newEmotionalStates
            convertDiaryEntriesToListModel()
        }
    }
}