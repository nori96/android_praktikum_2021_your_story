package com.example.yourstory.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.databinding.ReportsFragmentBinding
import org.joda.time.DateTime

class ReportsFragment : Fragment(), AdapterView.OnItemSelectedListener{

    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private lateinit var binding: ReportsFragmentBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var viewModel : ReportsViewModel
    private lateinit var hostFragmentNavController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ReportsFragmentBinding.inflate(inflater, container, false)
        if (container != null) {
            hostFragmentNavController = container.findNavController()
        }
        viewModel = ViewModelProvider(requireActivity())[ReportsViewModel::class.java]

        viewModel.reportsEntriesDataAsList.observe(viewLifecycleOwner, { newReports ->
            (recyclerView.adapter as ReportsAdapter).setData(newReports)
        })

        recyclerView = binding.reportRecyclerView
        recyclerView.adapter = ReportsAdapter(this)
        binding.fabReports.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_navigation_reports_to_createReportFragment)
        }

        viewModel.deleteState.observe(viewLifecycleOwner,{
            if(it == true){
                binding.deleteFabReports.visibility = View.VISIBLE
                binding.fabReports.visibility = View.INVISIBLE
            }else{
                binding.deleteFabReports.visibility = View.INVISIBLE
                binding.fabReports.visibility = View.VISIBLE
            }
        })

        binding.deleteFabReports.setOnClickListener{
            val reports = (recyclerView.adapter as ReportsAdapter).getSelectedEntries()
            viewModel.deleteReports(reports)
            (recyclerView.adapter as ReportsAdapter).deleteSelectedEntries()
        }

        //Init YearSpinner
        yearSpinner = binding.spinnerYearReports
        val arrayAdapterYear = ArrayAdapter(requireContext(),R.layout.spinner_custom,
            viewModel.years_items.value!!.toList())
        arrayAdapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = arrayAdapterYear
        yearSpinner.setSelection(viewModel.years_items.value!!.indexOf(DateTime.now().year().toString()))
        yearSpinner.onItemSelectedListener = this

        //Init MonthSpinner
        monthSpinner = binding.spinnerMonthReports
        val arrayadapterMonth = ArrayAdapter(requireContext(),R.layout.spinner_custom,
            viewModel.months_items.value!!.toList())
        arrayadapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = arrayadapterMonth
        monthSpinner.setSelection(DateTime.now().monthOfYear -1 )
        monthSpinner.onItemSelectedListener = this

        return binding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent!!.id){
            monthSpinner.id -> {viewModel.currentMonth.postValue(position +1)}
            yearSpinner.id -> {viewModel.currentYear.postValue(viewModel.years_items.value!![position])}
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}