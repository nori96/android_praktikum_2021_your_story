package com.example.yourstory.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.ReportEntry

class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = Repository(application)
    var reportsEntriesData : LiveData<List<ReportEntry>> = repository.readAllReportEntries()
}