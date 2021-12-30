package com.example.yourstory.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.yourstory.database.data.*
import com.example.yourstory.utils.DateEpochConverter
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.io.File
import java.util.*

class Repository(var application: Application){

    var googleAccount: GoogleSignInAccount
    var diaryEntryDao: DiaryEntryDao
    var emotionalStateDao: EmotionalStateDao
    var reportEntryDao: ReportEntryDao

    init {
        diaryEntryDao = Database.getDatabase(application).diaryEntryDao()
        emotionalStateDao = Database.getDatabase(application).emotionalStateDao()
        reportEntryDao = Database.getDatabase(application).reportEntryDao()
    }

    companion object{
        lateinit var googleDriveService: Drive
    }

    //Google Drive
    //TODO: Implement Drive-Access here


    fun uploadDataBaseToDrive(){
            if(!checkIfAppFolderExists()){
                initAppDriverFolders()
            }
            googleDriveService.files().create(com.google.api.services.drive.model.File().setName("yourstory_database_" + UUID.randomUUID()).setCreatedTime(
                com.google.api.client.util.DateTime(DateTime.now().toString())
            ),
                FileContent(null, File(Database.getDatabase(application).openHelper.writableDatabase.path))
            ).execute()
    }

    private fun initAppDriverFolders() {
        googleDriveService
    }

    private fun checkIfAppFolderExists(): Boolean {
        googleDriveService.files().list()
            .setQ("mimeType=application/vnd.sqlite3")
            .execute()
        return false
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