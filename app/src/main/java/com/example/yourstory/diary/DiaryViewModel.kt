package com.example.yourstory.diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.diary.detail.Date
import com.example.yourstory.utils.DateEpochConverter
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

//TODO: Limit Month Spinner to Existing Dates in Line 50
class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    var currentMonth = DateTime.now().monthOfYear().get()
    var currentYear = DateTime.now().year.toString()

    //SpinnerData
    var monthsItems = MutableLiveData<ArrayList<String>>(arrayListOf())
    var yearsItems =  MutableLiveData<ArrayList<String>>(arrayListOf())

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
            monthsItems.value!!.add(DateEpochConverter.monthIntToString(application,idx))
        }

        //Sets the years of the Spinner to the oldest Entry
        for(idx in 0.. 5){
            var prevYear = Calendar.getInstance()
            prevYear.add(Calendar.YEAR,idx * -1)
            yearsItems.value!!.add(prevYear.get(Calendar.YEAR).toString())
        }
    }

    private fun convertDiaryEntriesToListModel() {

        var newEntriesList = arrayListOf<DiaryListModel>()

        var dates = arrayListOf<Long>()

        //Filling dates
        for (diaryEntry in diaryEntriesCopy){
           val day = DateEpochConverter.convertDateTimeToEpoch(DateEpochConverter.convertEpochToDateTime(diaryEntry.date).withTime(0,0,0,0).toString())
            if(!dates.contains(day)){
                dates.add(day)
            }
        }
        for(emotionalState in emotionalStateEntriesCopy){
            val day = DateEpochConverter.convertDateTimeToEpoch(DateEpochConverter.convertEpochToDateTime(emotionalState.date).withTime(0,0,0,0).toString())
            if(!dates.contains(day)){
                dates.add(day)
            }
        }

        //Sort Entries so, the oldest Entry will be on top
        Collections.sort(dates,Collections.reverseOrder())

        for (dateInEpochTime in dates){
            var dateInDateTime = DateEpochConverter.convertEpochToDateTime(dateInEpochTime)

            //create bounds in epoch
            var lowerBound = DateEpochConverter.convertDateTimeToEpoch(DateEpochConverter.convertEpochToDateTime(dateInEpochTime).withTime(0,0,0,0).toString())
            var upperBound = DateEpochConverter.convertDateTimeToEpoch(DateEpochConverter.convertEpochToDateTime(dateInEpochTime).withTime(23,59,59,999).toString())

            //Get all emotional-states and diary-entries of the date
            var emotionalStatesOfTheDay = emotionalStateEntriesCopy.filter { emotionalState ->
                emotionalState.date in (lowerBound + 1) until upperBound
            }
            var diaryEntriesOfTheDay = diaryEntriesCopy.filter { diaryEntry ->
                diaryEntry.date in (lowerBound + 1) until upperBound
            }



            var year = dateInDateTime.year().get()
            var month = dateInDateTime.monthOfYear
            var day = dateInDateTime.dayOfMonth

            //No Emotional-States for the day
            if(emotionalStatesOfTheDay.isEmpty()) {
                newEntriesList.add(
                    DiaryListModel(
                        Date(day.toString(), month.toString(), year.toString()),
                        emotionalStatesOfTheDay.size + diaryEntriesOfTheDay.size,
                        0F,
                        0F,
                        0F,
                        0F,
                        0F,
                        0F,
                    )
                )
            }else{

                //Calculate Averages
                var joyAverage = 0F
                var angerAverage = 0F
                var surpriseAverage = 0F
                var sadnessAverage = 0F
                var disgustAverage = 0F
                var fearAverage = 0F

                for (emotionalState in emotionalStatesOfTheDay){
                    joyAverage += emotionalState.joy
                    angerAverage += emotionalState.anger
                    surpriseAverage += emotionalState.surprise
                    sadnessAverage += emotionalState.sadness
                    disgustAverage += emotionalState.disgust
                    fearAverage += emotionalState.fear
                }

                newEntriesList.add(
                    DiaryListModel(
                        Date(day.toString(), month.toString(), year.toString()),
                        emotionalStatesOfTheDay.size + diaryEntriesOfTheDay.size,
                        joyAverage,
                        angerAverage,
                        surpriseAverage,
                        sadnessAverage,
                        disgustAverage,
                        fearAverage,
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