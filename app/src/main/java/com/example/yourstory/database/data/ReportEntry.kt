package com.example.yourstory.database.data


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "report_entries_table")
class ReportEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val madeAt: Long,
    val period: String,
    val averageEmotionality: Float,
    val averageJoy: Float,
    val averageAnger: Float,
    val averageSurprise: Float,
    val averageSadness: Float,
    val averageDisgust: Float,
    val averageFear: Float,
)