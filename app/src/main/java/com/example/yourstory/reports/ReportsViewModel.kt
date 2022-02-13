package com.example.yourstory.reports

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.ReportEntry
import com.example.yourstory.utils.DateEpochConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import java.util.*

class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    val reportsEntriesDataAsList =  MutableLiveData(listOf<ReportEntry>())
    private val repository: Repository = Repository(application)
    var deleteState = MutableLiveData(false)
    var selectedItems = MutableLiveData(listOf<Int>())
    lateinit var reportsEntriesData : LiveData<List<ReportEntry>>

    //SpinnerData

    var currentMonth = MutableLiveData(DateTime.now().monthOfYear().get())
    var currentYear = MutableLiveData(DateTime.now().year.toString())

    private var currentMonthCopy = DateTime.now().monthOfYear().get()
    private var currentYearCopy = DateTime.now().year.toString()

    var monthsItems = MutableLiveData<ArrayList<String>>(arrayListOf())
    var yearsItems =  MutableLiveData<ArrayList<String>>(arrayListOf())

    init {

        //init Spinner Data
        for(idx in 1..12){
            monthsItems.value!!.add(DateEpochConverter.monthIntToString(application,idx))
        }

        //Sets the years of the Spinner to the oldest Entry
        for(idx in 0.. 5){
            val prevYear = Calendar.getInstance()
            prevYear.add(Calendar.YEAR,idx * -1)
            yearsItems.value!!.add(prevYear.get(Calendar.YEAR).toString())
        }

        currentMonth.observeForever{
            currentMonthCopy = it
            val filterDate = DateTime(currentYearCopy.toInt(),it,1,1,1,1,1)
            reportsEntriesData = repository.readAllReportsOfaMonth(filterDate.toString())

            reportsEntriesData.observeForever{ newReports ->
                reportsEntriesDataAsList.postValue(newReports)
            }
        }

        currentYear.observeForever{
            currentYearCopy = it
            val filterDate = DateTime(it.toInt(),currentMonthCopy,1,1,1,1,1)
            reportsEntriesData = repository.readAllReportsOfaMonth(filterDate.toString())

            reportsEntriesData.observeForever{ newReports ->
                reportsEntriesDataAsList.postValue(newReports)
            }
        }
    }

    fun reloadMonthData() {
        monthsItems.value!!.clear()
        for(idx in 1..12){
            monthsItems.value!!.add(DateEpochConverter.monthIntToString(getApplication() ,idx))
        }
    }

    fun deleteReports(reports: ArrayList<ReportEntry>) {
        viewModelScope.launch (Dispatchers.IO) {
            for (report in reports){
                repository.deleteReport(report.id)
            }
        }
    }
}