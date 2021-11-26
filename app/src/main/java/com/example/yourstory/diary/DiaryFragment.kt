package com.example.yourstory.diary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.databinding.DiaryFragmentBinding
import com.example.yourstory.reports.ReportModel
import com.example.yourstory.reports.ReportsAdapter
import com.example.yourstory.reports.ReportsViewModel

class DiaryFragment : Fragment() {



    companion object {
        fun newInstance() = DiaryFragment()
    }

    lateinit var binding : DiaryFragmentBinding
    private lateinit var viewModel: DiaryViewModel
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var views = inflater.inflate(R.layout.diary_fragment, container, false)

        recyclerView = views.findViewById(R.id.diary_recycler_view)
        recyclerView.adapter = DiaryAdapter()

        return views
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(DiaryViewModel::class.java)
        val diaryObserver = Observer<ArrayList<DiaryModel>> { newDiaries ->
            (recyclerView.adapter as DiaryAdapter).dataSet = newDiaries
        }

        viewModel.reports.observe(viewLifecycleOwner,diaryObserver)
    }

}