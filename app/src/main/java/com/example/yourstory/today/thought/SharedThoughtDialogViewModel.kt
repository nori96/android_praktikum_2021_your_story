package com.example.yourstory.today.thought

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.yourstory.database.Repository
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.utils.DateEpochConverter
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SharedThoughtDialogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository

    // TakePictureFragment specific data
    var pictureImage = MutableLiveData(Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888))
    var picSelected = MutableLiveData(false)

    // these values represent their null values
    var image = MutableLiveData(Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888))
    //var location = MutableLiveData("")
    var location = MutableLiveData(LatLng(0.0, 0.0))
    var audio = MutableLiveData("")
    var text = MutableLiveData("")

    init{
        repository = Repository(application)
    }

    fun checkIfAnySelected(): Boolean{
        return !(image.value!!.height == 1 && location.value!! == LatLng(0.0,0.0) && audio.value!!.isEmpty() && text.value!!.isEmpty())
    }
    fun confirmDiaryEntry(context: Context){
        var rndmUUID = UUID.randomUUID().toString()

        //Set empty string if there is no Image set
        if(image.value!!.height.equals(1)){
            rndmUUID = ""
        }else{
            var fos = context.openFileOutput("$rndmUUID.png",Context.MODE_PRIVATE)
            image.value!!.compress(Bitmap.CompressFormat.PNG,100,fos)
            fos.close()
        }
        viewModelScope.launch (Dispatchers.IO){
            repository.addDiaryEntry(DiaryEntry(
                0,
                repository.readLastEmotionalStateID(),
                text.value.toString(),
                rndmUUID, audio.value!!,
                location.value!!.latitude,
                location.value!!.longitude,
                DateEpochConverter.generateEpochDate()
            ))
            resetData()
        }
    }

    fun resetData() {
        //Reset TakePictureFragment-Data
        pictureImage.postValue(Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888))
        picSelected.postValue(false)

        //Reset ShareViewModel-Data
        image.postValue(Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888))
        location.postValue(LatLng(0.0,0.0))
        audio.postValue("")
        text.postValue("")
    }
}