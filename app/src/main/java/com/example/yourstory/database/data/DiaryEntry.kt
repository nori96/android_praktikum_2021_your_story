package com.example.yourstory.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries_table")
data class DiaryEntry (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val emotionalStateID: Int,
    val text: String,
    val image: String,
    val audio: String,
    val location: String,
    val date: String,
)