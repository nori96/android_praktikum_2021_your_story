package com.example.yourstory.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.database.data.ReportEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = Repository(application)
    var deleteState = MutableLiveData(false)
    var selectedItems = MutableLiveData(listOf<Int>())
    var reportsEntriesData : LiveData<List<ReportEntry>> = repository.readAllReportEntries()

    fun deleteReports(reports: ArrayList<ReportEntry>) {
        viewModelScope.launch (Dispatchers.IO) {
            for (report in reports){
                repository.deleteReport(report.id)
            }
        }
    }
}