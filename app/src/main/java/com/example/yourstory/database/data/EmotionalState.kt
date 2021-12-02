package com.example.yourstory.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emotional_states_table")
data class EmotionalState(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val joy: Int,
    val suprise: Int,
    val anger: Int,
    val sadness: Int,
    val fear: Int,
    val disgust: Int
)