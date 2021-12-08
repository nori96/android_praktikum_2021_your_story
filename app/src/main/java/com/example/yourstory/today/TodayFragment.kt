package com.example.yourstory.today

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.database.data.Entry
import com.example.yourstory.databinding.TodayFragmentBinding
import com.example.yourstory.today.thought.SharedThoughtDialogViewModel
import com.example.yourstory.utils.DateEpochConverter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class TodayFragment : Fragment() {

    private lateinit var hostFramentNavController: NavController
    private lateinit var viewModel: TodayViewModel
    private lateinit var sharedViewModel: SharedThoughtDialogViewModel
    private lateinit var likertFab: FloatingActionButton
    private lateinit var thoughtFab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private var fabClicked = false

    private lateinit var binding: TodayFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TodayFragmentBinding.inflate(inflater, container, false)

        if (container != null) {
            hostFramentNavController = container.findNavController()
        }

        //Setup the Recycler-View
        recyclerView = binding.recyclerViewTodayPage
        recyclerView.adapter = DiaryEntriesAdapter()

        viewModel = ViewModelProvider(requireActivity())[TodayViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]
        sharedViewModel.resetData()

        viewModel.todayDiaryEntryData.observe(viewLifecycleOwner, { newDiaryEntries ->
            //val todayStates = todayFilterEmotionalStateEntries(viewModel.todayEmotionalStateEntryData.value) as List<Entry>
            val todayEntries = todayFilterDiaryEntries(newDiaryEntries) as List<Entry>
            //val comb = (todayEntries+todayStates).sortedBy { it.date }
            (recyclerView.adapter as DiaryEntriesAdapter).setData(todayEntries)
            recyclerView.smoothScrollToPosition(newDiaryEntries.size)
        })

        viewModel.todayEmotionalStateEntryData.observe(viewLifecycleOwner, { newStates ->
            //val todayEntries = todayFilterDiaryEntries(viewModel.todayDiaryEntryData.value) as List<Entry>
            val todayStates = todayFilterEmotionalStateEntries(newStates) as List<Entry>
            //val comb = (todayEntries+todayStates).sortedBy { it.date }
            (recyclerView.adapter as DiaryEntriesAdapter).setData(todayStates)
            recyclerView.smoothScrollToPosition(newStates.size)
        })


        // its the same function like above...
        /*viewModel.todayViewData.observe(viewLifecycleOwner, object: androidx.lifecycle.Observer<List<DiaryEntry>>
        {
            override fun onChanged(t: List<DiaryEntry>?)
            {
                (recyclerView.adapter as DiaryEntriesAdapter).setData(todayFilter(t))
                t?.size?.let { recyclerView.smoothScrollToPosition(it) };
            }
        })*/
        likertFab = binding.likertFab
        thoughtFab = binding.thoughtFab

        //Setup Floating-Action-Button
        binding.rootFab.setOnClickListener {
            if (!fabClicked) {
                likertFab.visibility = View.VISIBLE
                thoughtFab.visibility = View.VISIBLE
            } else {
                likertFab.visibility = View.INVISIBLE
                thoughtFab.visibility = View.INVISIBLE
            }
            fabClicked = !fabClicked
        }
        binding.thoughtFab.setOnClickListener {
            hostFramentNavController.navigate(R.id.action_navigation_today_to_thought_dialog)
            fabClicked = false
        }
        binding.likertFab.setOnClickListener {
            hostFramentNavController.navigate(R.id.action_navigation_today_to_likertDialog)
            fabClicked = false
        }

        return binding.root
    }

    private fun todayFilterDiaryEntries(diaryEntries: List<DiaryEntry>?): List<DiaryEntry> {
        val filteredDiaryEntries: List<DiaryEntry> = diaryEntries!!.filter { diaryEntry ->
            DateEpochConverter.convertEpochToDateTime(diaryEntry.date).toString().contains(DateEpochConverter.generateIsoDateWithoutTime())
        }
        if(filteredDiaryEntries.isEmpty()){
            return listOf()
        }
        return filteredDiaryEntries
    }
    private fun todayFilterEmotionalStateEntries(emotionalStateEntries: List<EmotionalState>?): List<EmotionalState> {
        val filteredEmotionalStateEntries: List<EmotionalState> = emotionalStateEntries!!.filter { emotionalStateEntry ->
            DateEpochConverter.convertEpochToDateTime(emotionalStateEntry.date).toString().contains(DateEpochConverter.generateIsoDateWithoutTime())
        }
        if(filteredEmotionalStateEntries.isEmpty()){
            return listOf()
        }
        return filteredEmotionalStateEntries
    }
}