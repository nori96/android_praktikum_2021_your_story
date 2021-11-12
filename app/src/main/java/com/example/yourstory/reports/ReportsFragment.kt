package com.example.yourstory.reports

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.databinding.ActivityMainBinding
import com.example.yourstory.databinding.ReportsFragmentBinding
import com.example.yourstory.reports.view.ReportsAdapter

class ReportsFragment : Fragment() {

    private lateinit var binding: ReportsFragmentBinding
    lateinit var recyclerView: RecyclerView

    private var reports = arrayListOf<ReportModel>()
    companion object {
        fun newInstance() = ReportsFragment()
    }

    private lateinit var viewModel: ReportsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding = ReportsFragmentBinding.inflate(layoutInflater)
        var views = inflater.inflate(R.layout.reports_fragment, container, false)

        recyclerView = views.findViewById(R.id.report_recycler_view)
        //recyclerView = binding.reportRecyclerView
        recyclerView.adapter = ReportsAdapter(reports)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        return views
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)
        for (reportModel in reports){
            reports.removeFirst()
        }
        for (reportModel in viewModel.reports){
            reports.add(reportModel)
        }
    }
}