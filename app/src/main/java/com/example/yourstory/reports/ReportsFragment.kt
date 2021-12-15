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
import com.example.yourstory.databinding.ReportsFragmentBinding

class ReportsFragment : Fragment() {

    private lateinit var binding: ReportsFragmentBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var viewModel : ReportsViewModel
    private lateinit var hostFramentNavController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ReportsFragmentBinding.inflate(inflater, container, false)
        if (container != null) {
            hostFramentNavController = container.findNavController()
        }
        recyclerView = binding.reportRecyclerView
        recyclerView.adapter = ReportsAdapter()
        binding.fabReports?.setOnClickListener {
            hostFramentNavController.navigate(R.id.action_navigation_reports_to_createReportFragment)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)

        val reportsObserver = Observer<ArrayList<ReportListModel>> { newReports ->
            (recyclerView.adapter as ReportsAdapter).dataSet = newReports
        }

        viewModel.reports.observe(viewLifecycleOwner,reportsObserver)
    }
}