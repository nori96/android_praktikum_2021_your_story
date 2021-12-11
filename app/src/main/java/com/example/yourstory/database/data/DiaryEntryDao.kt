package com.example.yourstory.database.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DiaryEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addDiaryEntry(diaryEntry: DiaryEntry)

    @Query("SELECT * FROM diary_entries_table ORDER BY id ASC ")
    fun readAllEntriesSortedByID(): LiveData<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries_table ORDER BY date ASC")
    fun readAllEntriesSortedByDate(): LiveData<List<DiaryEntry>>

    @Query("SELECT * FROM emotional_states_table WHERE id = :emotionalStateID")
    fun getEmotionalStateOfDiaryEntry(emotionalStateID: Int): LiveData<List<EmotionalState>>


    @Query("SELECT * FROM diary_entries_table WHERE date BETWEEN :from AND :to")
    fun readAllEntriesBetweenDates(from:Long, to:Long): LiveData<List<DiaryEntry>>

    @Query("SELECT * FROM diary_entries_table WHERE date = (SELECT MIN(date) FROM diary_entries_table)")
    fun readOldestEntry(): DiaryEntry
}