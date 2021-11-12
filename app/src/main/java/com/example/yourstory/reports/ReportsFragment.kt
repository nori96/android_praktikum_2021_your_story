package com.example.yourstory.reports

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.databinding.ReportsFragmentBinding
import com.example.yourstory.reports.view.ReportsAdapter

class ReportsFragment : Fragment() {

    private lateinit var binding: ReportsFragmentBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var viewModel : ReportsViewModel

    companion object {
        fun newInstance() = ReportsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var views = inflater.inflate(R.layout.reports_fragment, container, false)

        recyclerView = views.findViewById(R.id.report_recycler_view)
        recyclerView.adapter = ReportsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this.context)



        return views
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)

        val reportsObserver = Observer<ArrayList<ReportModel>> { newReports ->
            (recyclerView.adapter as ReportsAdapter).dataSet = newReports
        }

        viewModel.reports.observe(viewLifecycleOwner,reportsObserver)
    }
}