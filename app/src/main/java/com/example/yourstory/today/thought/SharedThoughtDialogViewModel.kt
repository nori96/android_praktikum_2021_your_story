package com.example.yourstory.today.thought

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yourstory.R

class SharedThoughtDialogViewModel : ViewModel() {

    // these values represent their null values
    var image = MutableLiveData(0)
    var location = MutableLiveData(false)
    var audio = MutableLiveData(0)
    var text = MutableLiveData("")

    public fun resetData() {
        audio.value = 0
        location.value = false
        text.value = ""
        image.value = 0
    }
 }