package com.example.yourstory.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "emotional_states_table")
class EmotionalState(
    @PrimaryKey(autoGenerate = true) val id: Int,
    override val date: Long,
    val joy: Int,
    val surprise: Int,
    val anger: Int,
    val sadness: Int,
    val fear: Int,
    val disgust: Int
) : Entry()