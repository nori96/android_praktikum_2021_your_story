package com.example.yourstory.diary.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.database.data.Entry
import com.example.yourstory.databinding.DiaryDetailFragmentBinding
import kotlinx.android.synthetic.main.activity_main.*

class DiaryDetailFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val args: DiaryDetailFragmentArgs by navArgs()
    private lateinit var viewModel: DiaryDetailViewModel
    private lateinit var binding: DiaryDetailFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DiaryDetailFragmentBinding.inflate(layoutInflater,container,false)

        viewModel = ViewModelProvider(requireActivity())[DiaryDetailViewModel::class.java]

        recyclerView = binding.recyclerViewDiaryDetailPage
        recyclerView.adapter = DiaryDetailEntriesAdapter(this)

        viewModel.setDate(args.date.toString())

        viewModel.todayDiaryEntryData.observe(viewLifecycleOwner, { newDiaryEntries ->
            val todayEntries = newDiaryEntries as List<Entry>
            if(todayEntries.isEmpty()) {
                (recyclerView.adapter as DiaryDetailEntriesAdapter).removeDiaryEntries()
            }else {
                (recyclerView.adapter as DiaryDetailEntriesAdapter).setData(todayEntries)
            }
        })

        viewModel.todayEmotionalStateEntryData.observe(viewLifecycleOwner, { newStates ->
            val todayStates = newStates as List<Entry>
            if(todayStates.isEmpty()){
                (recyclerView.adapter as DiaryDetailEntriesAdapter).removeEmotionalStates()
            }else {
                (recyclerView.adapter as DiaryDetailEntriesAdapter).setData(todayStates)
            }
        })

        viewModel.deleteState.observe(viewLifecycleOwner,{
            if(it == true){
                binding.deleteFab.visibility = View.VISIBLE
            }else{
                binding.deleteFab.visibility = View.INVISIBLE
            }
        })

        binding.deleteFab.setOnClickListener {
            val entries = (recyclerView.adapter as DiaryDetailEntriesAdapter).getSelectedEntries()
            viewModel.deleteDiaryEntries(entries)
            (recyclerView.adapter as DiaryDetailEntriesAdapter).deleteSelectedEntries()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().custom_toolbar.title = args.date.toString()
    }

}