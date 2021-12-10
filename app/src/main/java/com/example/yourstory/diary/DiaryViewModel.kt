package com.example.yourstory.diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.diary.detail.Date
import com.example.yourstory.utils.DateEpochConverter
import org.joda.time.DateTime

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    // TODO: Implement the ViewModel


    private var _currentMonth = DateEpochConverter.monthIntToString(application,DateTime.now().monthOfYear)
    private var _currentYear = DateTime.now().year
    private val repository: Repository
    var diaryEntriesAsListModel = MutableLiveData<ArrayList<DiaryListModel>>()
    var diaryEntries : LiveData<List<DiaryEntry>>
    var emotionalStateEntries: LiveData<List<EmotionalState>>

    var emotionalStateEntriesCopy = listOf<EmotionalState>()
    var diaryEntriesCopy = listOf<DiaryEntry>()

    init {
        repository = Repository(application)
        diaryEntries = repository.readAllEntriesOfaMonth(DateTime.now().toString())
        emotionalStateEntries = repository.readEmotionalStatesOfAMonth(DateTime.now().toString())

        diaryEntries.observeForever { newDiaryEntries ->
            diaryEntriesCopy = newDiaryEntries
            convertDiaryEntriesToListModel()
        }

        emotionalStateEntries.observeForever{
            newEmotionalStates -> emotionalStateEntriesCopy = newEmotionalStates
            convertDiaryEntriesToListModel()
        }

    }

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
                //TODO: Ekel ist Gelb irgendwie?
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
}