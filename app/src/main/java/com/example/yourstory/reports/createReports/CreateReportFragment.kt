package com.example.yourstory.reports.createReports

import android.opengl.Visibility
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yourstory.R
import com.example.yourstory.databinding.CreateReportFragmentBinding
import com.example.yourstory.databinding.TodayFragmentBinding

class CreateReportFragment : Fragment() {

    private lateinit var viewModel: CreateReportViewModel
    private lateinit var binding: CreateReportFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CreateReportFragmentBinding.inflate(inflater, container, false)

        binding.createReportSelectCalendar.setOnClickListener {
            resetSelection()
            binding.createReportSelectCalendar.cardElevation = 30f
            binding.createReportsDateContent.visibility = View.VISIBLE
        }
        binding.createReportPieChart.setOnClickListener {
            resetSelection()
            binding.createReportPieChart.cardElevation = 30f
            binding.createReportsPieChartContent.visibility = View.VISIBLE
        }
        binding.createReportBarChart.setOnClickListener {
            resetSelection()
            binding.createReportBarChart.cardElevation = 30f
            binding.createReportsBarChartContent.visibility = View.VISIBLE
        }
        binding.createReportsExport.setOnClickListener {
            resetSelection()
            binding.createReportsExport.cardElevation = 30f
            binding.createReportsExportContent.visibility = View.VISIBLE
        }
        return binding.root
    }

    private fun resetSelection() {
        binding.createReportSelectCalendar.cardElevation = 0f
        binding.createReportPieChart.cardElevation = 0f
        binding.createReportBarChart.cardElevation = 0f
        binding.createReportsExport.cardElevation = 0f
        binding.createReportsDateContent.visibility = View.INVISIBLE
        binding.createReportsPieChartContent.visibility = View.INVISIBLE
        binding.createReportsBarChartContent.visibility = View.INVISIBLE
        binding.createReportsExportContent.visibility = View.INVISIBLE
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateReportViewModel::class.java)
    }

}