package com.example.yourstory.database

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.yourstory.database.data.*
import com.example.yourstory.utils.DateEpochConverter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import org.joda.time.DateTime
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class Repository(var application: Application){

    private var googleAccount: GoogleSignInAccount?
    var diaryEntryDao: DiaryEntryDao
    var emotionalStateDao: EmotionalStateDao
    var reportEntryDao: ReportEntryDao

    init {
        diaryEntryDao = Database.getDatabase(application).diaryEntryDao()
        emotionalStateDao = Database.getDatabase(application).emotionalStateDao()
        reportEntryDao = Database.getDatabase(application).reportEntryDao()
        googleAccount = GoogleSignIn.getLastSignedInAccount(application)
        if(googleAccount != null){
            signInToGoogle(googleAccount!!)
        }
    }

    companion object{
         var googleDriveService: Drive? = null
    }

    //Google Drive

    fun signInToGoogle(googleSignInAccount: GoogleSignInAccount){
        googleAccount = googleSignInAccount

        var credential = GoogleAccountCredential.usingOAuth2(application.applicationContext, setOf(DriveScopes.DRIVE_FILE,DriveScopes.DRIVE_APPDATA))
        credential.selectedAccount = googleSignInAccount.account

        googleDriveService = Drive.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential).setApplicationName("Your Story").build()
    }

    fun uploadDataBaseToDrive(){
        if(googleAccount != null && googleDriveService == null){
            signInToGoogle(googleAccount!!)
        }

        val fileMetadata = com.google.api.services.drive.model.File()
        fileMetadata.name = "yourstory_database_" + UUID.randomUUID()
        //fileMetadata.parents = listOf("appDataFolder")
        fileMetadata.createdTime = com.google.api.client.util.DateTime(DateTime.now().toString())

        var filecontent = FileContent(
            "application/database",
           File(Database.getDatabase(application).openHelper.writableDatabase.path)
        )

        googleDriveService!!.files().create(fileMetadata,filecontent)
            .setFields("id")
            .execute()
    }

    fun downloadLatestDB(fileID: String): File {
        var database = File(Database.getDatabase(application.baseContext).openHelper.writableDatabase.path + "_backup")
        database.createNewFile()

        var outputStream = FileOutputStream(database) as OutputStream
        googleDriveService!!.files().get(fileID)
            .executeAndDownloadTo(outputStream)
        outputStream.flush()
        outputStream.close()
        return database
    }

    fun getLatestDBMetadata(): com.google.api.services.drive.model.File? {
        var files = googleDriveService!!.files().list()
            .setSpaces("appDataFolder")
            .setFields("nextPageToken, files (id,name,createdTime)")
            .setPageSize(10)
            .execute()
        if(files.files.isEmpty()){
            return null
        }
        return files.files.first()
    }

    fun checkIfDataBaseExist(): Boolean{
        if(googleDriveService == null){
            return false
        }
        var files = googleDriveService!!.files().list()
            .setSpaces("appDataFolder")
            .setFields("nextPageToken, files (id,name,createdTime)")
            .setPageSize(10)
            .execute()
        return files.files.isNotEmpty()
    }

    //Google
    fun getGoogleAccount() : GoogleSignInAccount?{
        if(googleAccount == null){
            return null;
        }
        return googleAccount
    }

    fun signOutFromGoogle(){
        googleAccount = null;
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

    fun deleteDiaryEntry(diaryEntryID: Int){
        diaryEntryDao.deleteDiaryEntryById(diaryEntryID)
    }

    fun addReportEntry(reportEntry: ReportEntry) {
        reportEntryDao.addReport(reportEntry)
    }

    fun deleteReport(id: Int) {
        reportEntryDao.deleteReport(id)
    }

    fun readAllReportEntries() : LiveData<List<ReportEntry>>{
        return reportEntryDao.readAllReportsSortedByDate()
    }

    fun readAllReportsOfaMonth(isoDate: String):LiveData<List<ReportEntry>>{
        var startEpoch = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).dayOfMonth().withMinimumValue().withTime(0,0,0,0).toString())
        var endEpoch = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).dayOfMonth().withMaximumValue().withTime(23,59,59,999).toString())
        return reportEntryDao.readAllReportsBetweenDates(startEpoch,endEpoch)
    }

    fun readAllEntriesOfaDate(isoDate: String): LiveData<List<DiaryEntry>>{
        val epochCurrentDateStart = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).withTime(0, 0, 0, 0).toDateTimeISO().toString())
        val epochCurrentDateEnd = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).withTime(23, 59, 59, 999).toDateTimeISO().toString())
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

    fun deleteEmotionalState(emotionalStateID: Int){
        emotionalStateDao.deleteEmotionalStateByID(emotionalStateID)
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
        val epochCurrentDateStart = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).withTime(0, 0, 0, 0).toString())
        val epochCurrentDateEnd = DateEpochConverter.convertDateTimeToEpoch(DateTime(isoDate).withTime(23, 59, 59, 999).toString())
        return emotionalStateDao.readAllEmotionalSatesBetweenDates(epochCurrentDateStart,epochCurrentDateEnd)
    }

    fun readoldestEmotionalStateDate(): EmotionalState{
        return emotionalStateDao.readOldestEmotionalState()
    }
}