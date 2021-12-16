package com.example.yourstory.database

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yourstory.database.data.*
import com.example.yourstory.utils.DateEpochConverter
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.time.ZoneOffset.UTC
import java.util.*

class Repository(application: Application){

    var diaryEntryDao: DiaryEntryDao
    var emotionalStateDao: EmotionalStateDao
    var reportEntryDao: ReportEntryDao

    init {
        diaryEntryDao = Database.getDatabase(application).diaryEntryDao()
        emotionalStateDao = Database.getDatabase(application).emotionalStateDao()
        reportEntryDao = Database.getDatabase(application).reportEntryDao()
    }

    //Diary Entry functions

    fun readAllEntriesSortedByID(): LiveData<List<DiaryEntry>> {
        return diaryEntryDao.readAllEntriesSortedByID()
    }

    fun readAllEntriesSortedByDate(): LiveData<List<DiaryEntry>>{
        return diaryEntryDao.readAllEntriesSortedByDate()
    }

    fun getEmotionalStateOfDiaryEntry(diaryEntry: DiaryEntry): LiveData<List<EmotionalState>>{
        return diaryEntryDao.getEmotionalStateOfDiaryEntry(diaryEntry.emotionalStateID)
    }

     fun addDiaryEntry(diaryEntry: DiaryEntry){
        diaryEntryDao.addDiaryEntry(diaryEntry)
    }

    fun addReportEntry(reportEntry: ReportEntry) {
        reportEntryDao.addReport(reportEntry)
    }
    fun readAllReportEntries() : LiveData<List<ReportEntry>>{
        return reportEntryDao.readAllReportsSortedByDate()
    }

    fun readAllEntriesOfaDate(isoDate: String): LiveData<List<DiaryEntry>>{
        val epochCurrentDateStart = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate, DateTimeZone.UTC).withTime(0, 0, 0, 0).toDateTimeISO().toString())
        val epochCurrentDateEnd = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate, DateTimeZone.UTC).withTime(23, 59, 59, 999).toDateTimeISO().toString())
        return diaryEntryDao.readAllEntriesBetweenDates(epochCurrentDateStart,epochCurrentDateEnd)
    }

    fun readAllEntriesOfaMonth(isoDate: String): LiveData<List<DiaryEntry>>{
            var startEpoch = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).dayOfMonth().withMinimumValue().withTime(0,0,0,0).toString())
            var endEpoch = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).dayOfMonth().withMaximumValue().withTime(23,59,59,999).toString())

        return diaryEntryDao.readAllEntriesBetweenDates(startEpoch,endEpoch)
    }

    fun readOldestEntry(): DiaryEntry {
        return diaryEntryDao.readOldestEntry()
    }

    //Emotional State functions

    fun readAllEmotionalStatesSortedByID(): LiveData<List<EmotionalState>>{
        return emotionalStateDao.readAllEmotionalStatesSortedByID()
    }

    fun readAllEmotionalStatesSortedByDate(): LiveData<List<EmotionalState>>{
        return emotionalStateDao.readAllEmotionalStatesSortedByDate()
    }

     fun addEmotionalState(emotionalState: EmotionalState){
        emotionalStateDao.addEmotionalState(emotionalState)
    }

    fun readEmotionalStatesOfAMonth(isoDate: String): LiveData<List<EmotionalState>> {
        var startEpoch = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).dayOfMonth().withMinimumValue().withTime(0,0,0,0).toString())
        var endEpoch = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).dayOfMonth().withMaximumValue().withTime(23,59,59,999).toString())

        return emotionalStateDao.readAllEmotionalSatesBetweenDates(startEpoch,endEpoch)
    }
    fun readAllEmotionalStatesBetweenDates(start: Long, end: Long): LiveData<List<EmotionalState>> {
        return emotionalStateDao.readAllEmotionalSatesBetweenDates(start, end)
    }

    fun readLastEmotionalStateID(): Int {
        var emotionalState = emotionalStateDao.readAllEmotionalStatesSortedByDateWithoutLiveData()
        if(emotionalState.isEmpty()){
            return -1
        }else {
            return emotionalState[0].id
        }
    }

    fun readAllEmotionalStatesOfADate(isoDate: String): LiveData<List<EmotionalState>>{
        val epochCurrentDateStart = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate, DateTimeZone.UTC).withTime(0, 0, 0, 0).toString())
        val epochCurrentDateEnd = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate, DateTimeZone.UTC).withTime(23, 59, 59, 999).toString())
        return emotionalStateDao.readAllEmotionalSatesBetweenDates(epochCurrentDateStart,epochCurrentDateEnd)
    }

    fun readoldestEmotionalStateDate(): EmotionalState{
        return emotionalStateDao.readOldestEmotionalState()
    }
}