package com.example.yourstory.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "diary_entries_table")
data class DiaryEntry (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val emotionalStateID: Int,
    val text: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray,
    val audio: String,
    val location: String,
    val date: String,
)