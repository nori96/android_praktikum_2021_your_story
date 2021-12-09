package com.example.yourstory.diary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.database.data.DiaryEntry
import com.example.yourstory.databinding.DiaryFragmentBinding
import com.example.yourstory.databinding.TodayFragmentBinding
import com.example.yourstory.diary.detail.Date
import com.example.yourstory.utils.DateEpochConverter

class DiaryFragment : Fragment(), DiaryAdapter.OnDiaryClickListener{



    companion object {
        fun newInstance() = DiaryFragment()
    }

    private lateinit var hostFramentNavController: NavController
    lateinit var binding : DiaryFragmentBinding
    private lateinit var viewModel: DiaryViewModel
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DiaryFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DiaryViewModel::class.java)

        if (container != null) {
            hostFramentNavController = container.findNavController()
        }

        //Create RecyclerView
        recyclerView = binding.diaryRecyclerView
        recyclerView.adapter = DiaryAdapter(this)

        viewModel.diaryEntriesAsListModel.observe(viewLifecycleOwner,{
            newDiaryEntrys -> (recyclerView.adapter as DiaryAdapter).setData(newDiaryEntrys)
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onNoteClick(position: Int) {
        var clickedListItem = viewModel.diaryEntriesAsListModel.value!![position]

        hostFramentNavController.navigate(DiaryFragmentDirections.actionNavigationDiaryToDiaryDetailFragment(clickedListItem.date))
    }
}