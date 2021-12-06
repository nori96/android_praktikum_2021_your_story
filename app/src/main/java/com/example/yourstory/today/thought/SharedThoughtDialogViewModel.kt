package com.example.yourstory.today.thought

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yourstory.R
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.utils.DateEpochConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SharedThoughtDialogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository

    // these values represent their null values
    var image = MutableLiveData(ByteArray(0))
    var location = MutableLiveData("")
    var audio = MutableLiveData("")
    var text = MutableLiveData("")

    init{
        repository = Repository(application)
    }
    fun confirmDiaryEntry(){
        viewModelScope.launch (Dispatchers.IO){
            repository.addDiaryEntry(DiaryEntry(0, repository.readLastEmotionalStateID(),
                text.value.toString(), image.value!!, audio.value!!, location.value!!,
                DateEpochConverter.generateEpochDate()
            ))
        }
    }

    public fun resetData() {
        image.value = ByteArray(0)
        location.value = ""
        audio.value = ""
        text.value = ""
    }
}