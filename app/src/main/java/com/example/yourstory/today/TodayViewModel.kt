package com.example.yourstory.today

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yourstory.R

class TodayViewModel : ViewModel() {

    var entries = MutableLiveData<List<DiaryEntry>>(
        listOf(
            DiaryEntry("asdf asdf  fds df d asdfes dfasdfr f sadf vsad sdf asdf  fds df d asdfes dfasdfr f sadf vsad", R.drawable.beach, true, R.raw.alan_watts),
            DiaryEntry("asdf asdf aewf chsgdsahr awre dfds a sad sdf asdf  fds df d asdfes dfasdfr f sadf vsad sdf sfd s fd f", 0, true, R.raw.alan_watts),
            DiaryEntry("asdf asdf asdf asdf asdf asdf ", R.drawable.beach, true, 0),
            DiaryEntry("asdffdsa f dsafdsafdsa dsf dsaf", R.drawable.beach, false, 0),
            DiaryEntry("asdf", 0, false, 0),
        )
    )
}