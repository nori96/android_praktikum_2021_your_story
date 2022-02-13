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

    private lateinit var yearSpinner: Spinner
    lateinit var monthSpinner: Spinner
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
            viewModel.yearsItems.value!!.toList())
        arrayAdapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = arrayAdapterYear
        yearSpinner.setSelection(viewModel.yearsItems.value!!.indexOf(DateTime.now().year().toString()))
        yearSpinner.onItemSelectedListener = this
        monthSpinner = binding.spinnerMonthReports
        initMonthSpinner()

        return binding.root
    }

    private fun initMonthSpinner() {
        val arrayAdapterMonth = ArrayAdapter(requireContext(),R.layout.spinner_custom,
            viewModel.monthsItems.value!!.toList())
        arrayAdapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = arrayAdapterMonth
        monthSpinner.setSelection(DateTime.now().monthOfYear -1 )
        monthSpinner.onItemSelectedListener = this
    }

    override fun onResume() {
        super.onResume()
        val pos  = monthSpinner.selectedItemPosition
        viewModel.reloadMonthData()
        val arrayAdapterMonth = ArrayAdapter(requireContext(),R.layout.spinner_custom,
            viewModel.monthsItems.value!!.toList())
        arrayAdapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = arrayAdapterMonth
        monthSpinner.setSelection(pos)
        monthSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent!!.id){
            binding.spinnerMonthReports.id -> {viewModel.currentMonth.postValue(position +1)}
            yearSpinner.id -> {viewModel.currentYear.postValue(viewModel.yearsItems.value!![position])}
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}