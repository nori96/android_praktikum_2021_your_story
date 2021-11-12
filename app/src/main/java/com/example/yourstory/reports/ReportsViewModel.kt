package com.example.yourstory.reports

import androidx.lifecycle.ViewModel

class ReportsViewModel : ViewModel() {

    private var _currentMonth = "April"
    private var _currentYear = "2021"
    private var _reports = arrayListOf(
        ReportModel("03.04.21","10.04.21 - 15.04.21",3.4),
        ReportModel("04.04.21","13.04.21 - 18.04.21",2.4),
        ReportModel("05.04.21","20.04.21 - 26.04.21",5.0)
    )

    val currentMonth: String
        get() = _currentMonth
    val currentYear: String
        get() = _currentYear
    val reports: List<ReportModel>
        get() = _reports

}