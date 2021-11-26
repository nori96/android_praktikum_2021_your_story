package com.example.yourstory.diary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yourstory.reports.ReportModel

class DiaryViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private var _currentMonth = "April"
    private var _currentYear = "2021"
    private var _diaries = MutableLiveData<ArrayList<DiaryModel>>(
        arrayListOf(
            DiaryModel("03.04.21",11,1f,2f,3f,4f,5f,5f),
            DiaryModel("04.04.21",22,2f,5f,1f,4f,3f,1f),
            DiaryModel("05.04.21",5,3f,1f,1f,2f,3f,2f)
        )
    )

    val currentMonth: String
        get() = _currentMonth
    val currentYear: String
        get() = _currentYear
    val reports: MutableLiveData<ArrayList<DiaryModel>>
        get() = _diaries
}