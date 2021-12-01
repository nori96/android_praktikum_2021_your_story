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
import com.example.yourstory.databinding.DiaryFragmentBinding
import com.example.yourstory.databinding.TodayFragmentBinding
import com.example.yourstory.diary.detail.Date

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

        if (container != null) {
            hostFramentNavController = container.findNavController()
        }

        //Create RecyclerView
        recyclerView = binding.diaryRecyclerView
        recyclerView.adapter = DiaryAdapter(this)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(DiaryViewModel::class.java)
        val diaryObserver = Observer<ArrayList<DiaryListModel>> { newDiaries ->
            (recyclerView.adapter as DiaryAdapter).dataSet = newDiaries
        }

        viewModel.reports.observe(viewLifecycleOwner,diaryObserver)
    }

    override fun onNoteClick(position: Int) {
        var clickedListItem = viewModel.reports.value?.get(position)

        if (clickedListItem != null) {
            hostFramentNavController.navigate(DiaryFragmentDirections.actionNavigationDiaryToDiaryDetailFragment(clickedListItem.date))
        }
    }
}