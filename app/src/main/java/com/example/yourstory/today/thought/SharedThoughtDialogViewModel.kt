package com.example.yourstory.today.thought

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedThoughtDialogViewModel : ViewModel() {
    var hasImage = MutableLiveData(false)
    var hasLocation = MutableLiveData(false)
    var hasAudio = MutableLiveData(false)
    var hasText = MutableLiveData(false)
 }