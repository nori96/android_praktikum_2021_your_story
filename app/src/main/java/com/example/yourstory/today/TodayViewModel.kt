package com.example.yourstory.today

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yourstory.R

class TodayViewModel : ViewModel() {

    private var todayViewData = MutableLiveData<ArrayList<DiaryEntry>>()

    fun getTodayViewModel(): LiveData<ArrayList<DiaryEntry>> {
        return todayViewData
    }
    init {
        var initList =ArrayList<DiaryEntry>()
        initList.add(DiaryEntry("I only wanted to send out a warning, against the needless waste created by capitalism without philosophy, the needless colonisation of planets, the needless circulation of slanted media, and needlessly tall buildings that symbolise all of this!", R.drawable.beach, true, R.raw.alan_watts))
        todayViewData.value = initList
        Log.i("livedata", (todayViewData.value)?.size.toString())
    }

    fun addEntry(text: String, image: Int, location: Boolean, audio: Int) {
        var newList = ArrayList<DiaryEntry>()
        newList.addAll(todayViewData.value?.toList()!!)
        newList.add(DiaryEntry(text, image, location, audio))
        todayViewData.value = newList
    }
    /*var entries = MutableLiveData(
        listOf(
            DiaryEntry("asdf asdf  fds df d asdfes dfasdfr f sadf vsad sdf asdf  fds df d asdfes dfasdfr f sadf vsad", R.drawable.beach, true, R.raw.alan_watts),
            DiaryEntry("asdf asdf aewf chsgdsahr awre dfds a sad sdf asdf  fds df d asdfes dfasdfr f sadf vsad sdf sfd s fd f", 0, true, R.raw.alan_watts),
            DiaryEntry("asdf asdf asdf asdf asdf asdf ", R.drawable.beach, true, 0),
            DiaryEntry("asdffdsa f dsafdsafdsa dsf dsaf", R.drawable.beach, false, 0),
            DiaryEntry("asdf", 0, false, 0),
        )
    )*/
}