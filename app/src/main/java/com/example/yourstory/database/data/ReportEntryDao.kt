package com.example.yourstory.database.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReportEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addReport(report: ReportEntry)

    @Query("DELETE FROM report_entries_table WHERE id = :reportId")
    fun deleteReport(reportId: Int)

    @Query( "SELECT * FROM report_entries_table ORDER BY madeAt ASC")
    fun readAllReportsSortedByDate(): LiveData<List<ReportEntry>>

    @Query("SELECT * FROM report_entries_table WHERE madeAt BETWEEN :from AND :to")
    fun readAllReportsBetweenDates(from:Long, to:Long): LiveData<List<ReportEntry>>
}