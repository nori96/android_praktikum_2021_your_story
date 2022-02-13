package com.example.yourstory.diary

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.databinding.DiaryFragmentBinding
import org.joda.time.DateTime

class DiaryFragment : Fragment(), DiaryAdapter.OnDiaryClickListener, AdapterView.OnItemSelectedListener {

    private lateinit var hostFramentNavController: NavController
    lateinit var binding : DiaryFragmentBinding
    private lateinit var viewModel: DiaryViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var yearSpinner: Spinner
    lateinit var monthSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DiaryFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DiaryViewModel::class.java]

        //Init YearSpinner
        yearSpinner = binding.spinnerYear
        val arrayAdapterYear = ArrayAdapter(requireContext(),R.layout.spinner_custom,
            viewModel.yearsItems.value!!.toList())
        arrayAdapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = arrayAdapterYear
        yearSpinner.setSelection(viewModel.yearsItems.value!!.indexOf(DateTime.now().year().toString()))
        yearSpinner.onItemSelectedListener = this
        monthSpinner = binding.spinnerMonth
        initMonthSpinner()


        if (container != null) {
            hostFramentNavController = container.findNavController()
        }

        //Create RecyclerView
        recyclerView = binding.diaryRecyclerView
        recyclerView.adapter = DiaryAdapter(this)



        viewModel.diaryEntriesAsListModel.observe(viewLifecycleOwner, {
            newDiaryEntrys -> (recyclerView.adapter as DiaryAdapter).setData(newDiaryEntrys)
        })

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

    override fun onNoteClick(position: Int) {
        val clickedListItem = viewModel.diaryEntriesAsListModel.value!![position]

        hostFramentNavController.navigate(DiaryFragmentDirections.actionNavigationDiaryToDiaryDetailFragment(clickedListItem.date))
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent!!.id){
            binding.spinnerMonth.id -> {viewModel.currentMonth = position +1
            viewModel.fetchFilteredData()}
            yearSpinner.id -> {viewModel.currentYear = viewModel.yearsItems.value!![position]
            viewModel.fetchFilteredData()}
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}