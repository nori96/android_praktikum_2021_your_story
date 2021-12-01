package com.example.yourstory.reports

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportsViewModel : ViewModel() {

    private var _currentMonth = "April"
    private var _currentYear = "2021"
    private var _reports = MutableLiveData<ArrayList<ReportListModel>>(
        arrayListOf(
        ReportListModel("03.04.21","10.04.21 - 15.04.21",3.4,1f,2f,3f,4f,5f,5f),
        ReportListModel("04.04.21","13.04.21 - 18.04.21",2.4,2f,5f,1f,4f,3f,1f),
        ReportListModel("05.04.21","20.04.21 - 26.04.21",5.0,3f,1f,1f,2f,3f,2f)
        )
    )

    val currentMonth: String
        get() = _currentMonth
    val currentYear: String
        get() = _currentYear
    val reports: MutableLiveData<ArrayList<ReportListModel>>
        get() = _reports

}