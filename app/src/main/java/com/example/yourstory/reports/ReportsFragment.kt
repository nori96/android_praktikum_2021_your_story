package com.example.yourstory.reports

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
import com.example.yourstory.database.data.Entry
import com.example.yourstory.databinding.ReportsFragmentBinding
import com.example.yourstory.today.DiaryEntriesAdapter

class ReportsFragment : Fragment() {

    private lateinit var binding: ReportsFragmentBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var viewModel : ReportsViewModel
    private lateinit var hostFragmentNavController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ReportsFragmentBinding.inflate(inflater, container, false)
        if (container != null) {
            hostFragmentNavController = container.findNavController()
        }
        viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)

        viewModel.reportsEntriesData.observe(viewLifecycleOwner, { newReports ->
            (recyclerView.adapter as ReportsAdapter).setData(newReports)
        })

        recyclerView = binding.reportRecyclerView
        recyclerView.adapter = ReportsAdapter()
        binding.fabReports?.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_navigation_reports_to_createReportFragment)
        }
        return binding.root
    }
}