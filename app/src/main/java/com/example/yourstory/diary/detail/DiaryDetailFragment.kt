package com.example.yourstory.diary.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.MainActivity
import com.example.yourstory.R
import com.example.yourstory.database.data.Entry
import com.example.yourstory.databinding.ActivityMainBinding
import com.example.yourstory.databinding.DiaryDetailFragmentBinding
import com.example.yourstory.today.DiaryEntriesAdapter
import kotlinx.android.synthetic.main.activity_main.*

class DiaryDetailFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val args: DiaryDetailFragmentArgs by navArgs()
    private lateinit var viewModel: DiaryDetailViewModel
    private lateinit var binding: DiaryDetailFragmentBinding

    companion object {
        fun newInstance() = DiaryDetailFragment()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DiaryDetailFragmentBinding.inflate(layoutInflater,container,false)

        viewModel = ViewModelProvider(this)[DiaryDetailViewModel::class.java]
        viewModel.setDate(args.date.toString())

        recyclerView = binding.recyclerViewDiaryDetailPage
        recyclerView.adapter = DiaryEntriesAdapter()

        viewModel.todayDiaryEntryData.observe(viewLifecycleOwner, { newDiaryEntries ->
            val todayEntries = newDiaryEntries as List<Entry>
            (recyclerView.adapter as DiaryEntriesAdapter).setData(todayEntries)
        })

        viewModel.todayEmotionalStateEntryData.observe(viewLifecycleOwner, { newStates ->
            val todayStates = newStates as List<Entry>
            (recyclerView.adapter as DiaryEntriesAdapter).setData(todayStates)
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().custom_toolbar.title = args.date.toString()
        // TODO: Use the ViewModel
    }

}