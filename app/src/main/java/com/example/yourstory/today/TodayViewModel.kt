package com.example.yourstory.today

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yourstory.R

class TodayViewModel : ViewModel() {

    var entries = MutableLiveData<List<DiaryEntry>>(
        listOf(
            DiaryEntry("asdf asdf  fds df d asdfes dfasdfr f sadf vsad", R.drawable.beach),
            DiaryEntry("asdf asdf aewf chsgdsahr awre dfds a", 0),
            DiaryEntry("asdf asdf asdf asdf asdf asdf ", R.drawable.beach),
            DiaryEntry("asdffdsa f dsafdsafdsa dsf dsaf", R.drawable.beach),
            DiaryEntry("asdf", R.drawable.beach),
        )
    )
}