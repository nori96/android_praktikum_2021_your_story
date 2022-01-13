package com.example.yourstory.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*

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