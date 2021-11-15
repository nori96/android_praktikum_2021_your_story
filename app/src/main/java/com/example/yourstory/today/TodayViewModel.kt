package com.example.yourstory.today

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yourstory.R

class TodayViewModel : ViewModel() {

    var entries = MutableLiveData<List<DiaryEntry>>(
        listOf(
            DiaryEntry("asdf asdf  fds df d asdfes dfasdfr f sadf vsad", R.drawable.beach, true, true),
            DiaryEntry("asdf asdf aewf chsgdsahr awre dfds a", 0, true, true),
            DiaryEntry("asdf asdf asdf asdf asdf asdf ", R.drawable.beach, true, false),
            DiaryEntry("asdffdsa f dsafdsafdsa dsf dsaf", R.drawable.beach, false, true),
            DiaryEntry("asdf", 0, false, false),
        )
    )
}