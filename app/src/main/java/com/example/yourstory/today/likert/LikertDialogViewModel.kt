package com.example.yourstory.today.likert

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourstory.database.Database
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.EmotionalState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LikertDialogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository

    init{
        repository = Repository(application)
    }

    fun addEmotionalState(emotionalState: EmotionalState){
        viewModelScope.launch (Dispatchers.IO){
            repository.addEmotionalState(emotionalState)
        }
    }
}