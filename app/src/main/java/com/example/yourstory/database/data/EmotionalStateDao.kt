package com.example.yourstory.database.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EmotionalStateDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addEmotionalState(emotionalState: EmotionalState)

    @Query( "SELECT * FROM emotional_states_table ORDER BY id ASC")
    fun readAllEmotionalStatesSortedByID(): LiveData<List<EmotionalState>>

    @Query( "SELECT * FROM emotional_states_table ORDER BY date ASC")
    fun readAllEmotionalStatesSortedByDate(): LiveData<List<EmotionalState>>

}