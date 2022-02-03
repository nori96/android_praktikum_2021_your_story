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
import com.example.yourstory.database.data.Entry
import com.example.yourstory.databinding.TodayFragmentBinding
import com.example.yourstory.today.thought.SharedThoughtDialogViewModel
import com.example.yourstory.utils.BackupManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
    ): View {

        BackupManager(requireContext()).initDB()

        binding = TodayFragmentBinding.inflate(inflater, container, false)

        if (container != null) {
            hostFramentNavController = container.findNavController()
        }
        recyclerView = binding.recyclerViewTodayPage


        viewModel = ViewModelProvider(requireActivity())[TodayViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]
        sharedViewModel.resetData()
        recyclerView.adapter = DiaryEntriesAdapter(this)

        viewModel.todayDiaryEntryData.observe(viewLifecycleOwner, { newDiaryEntries ->
            val todayEntries = newDiaryEntries as List<Entry>
            if(todayEntries.isEmpty()) {
                (recyclerView.adapter as DiaryEntriesAdapter).removeDiaryEntries()
            }else {
                (recyclerView.adapter as DiaryEntriesAdapter).setData(todayEntries)
            }
        })

        viewModel.todayEmotionalStateEntryData.observe(viewLifecycleOwner, { newStates ->
            val todayStates = newStates as List<Entry>
            if(todayStates.isEmpty()){
                (recyclerView.adapter as DiaryEntriesAdapter).removeEmotionalStates()
            }else {
                (recyclerView.adapter as DiaryEntriesAdapter).setData(todayStates)
            }
        })

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

        viewModel.deleteState.observe(viewLifecycleOwner,{
            if(it == true){
                binding.deleteFab.visibility = View.VISIBLE
                binding.rootFab.visibility = View.INVISIBLE
            }else{
                binding.deleteFab.visibility = View.INVISIBLE
                binding.rootFab.visibility = View.VISIBLE
            }
        })

        binding.deleteFab.setOnClickListener {
            val entries = (recyclerView.adapter as DiaryEntriesAdapter).getSelectedEntries()
            viewModel.deleteDiaryEntries(entries)
            (recyclerView.adapter as DiaryEntriesAdapter).deleteSelectedEntries()
        }
        return binding.root
    }
    // works not specific enough, maybe add this code to graph transitions?
    override fun onPause() {
        super.onPause()
        //viewModel.todayMediaPlayer!!.pause()
        //viewModel.mediaPlayerRunning.value = false
        //viewModel.currentAudioTrack.value = ""
    }
}