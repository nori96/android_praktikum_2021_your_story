package com.example.yourstory.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries_table")
class DiaryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val emotionalStateID: Int,
    val text: String,
    val image: String,
    val audio: String,
    val locationLat: Double,
    val locationLong: Double,
    override var date: Long,
) : Entry()