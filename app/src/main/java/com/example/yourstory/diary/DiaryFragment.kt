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

class DiaryFragment : Fragment(), DiaryAdapter.OnDiaryClickListener, AdapterView.OnItemSelectedListener{

    companion object {
        fun newInstance() = DiaryFragment()
    }

    private lateinit var hostFramentNavController: NavController
    lateinit var binding : DiaryFragmentBinding
    private lateinit var viewModel: DiaryViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var monthSpinner: Spinner
    lateinit var yearSpinner: Spinner


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DiaryFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DiaryViewModel::class.java)

        //Init YearSpinner
        yearSpinner = binding.spinnerYear!!
        var arrayAdapterYear = ArrayAdapter(requireContext(),R.layout.spinner_custom,
            viewModel.years_items.value!!.toList())
        arrayAdapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = arrayAdapterYear
        yearSpinner.setSelection(viewModel.years_items.value!!.indexOf(DateTime.now().year().toString()))
        yearSpinner.onItemSelectedListener = this

        //Init MonthSpinner
        monthSpinner = binding.spinnerMonth!!
        var arrayadapterMonth = ArrayAdapter(requireContext(),R.layout.spinner_custom,
            viewModel.months_items.value!!.toList())
        arrayadapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = arrayadapterMonth
        monthSpinner.setSelection(DateTime.now().monthOfYear -1 )
        monthSpinner.onItemSelectedListener = this

        if (container != null) {
            hostFramentNavController = container.findNavController()
        }

        //Create RecyclerView
        recyclerView = binding.diaryRecyclerView
        recyclerView.adapter = DiaryAdapter(this)



        viewModel.diaryEntriesAsListModel.observe(viewLifecycleOwner,{
            newDiaryEntrys -> (recyclerView.adapter as DiaryAdapter).setData(newDiaryEntrys)
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onNoteClick(position: Int) {
        var clickedListItem = viewModel.diaryEntriesAsListModel.value!![position]

        hostFramentNavController.navigate(DiaryFragmentDirections.actionNavigationDiaryToDiaryDetailFragment(clickedListItem.date))
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent!!.id){
            monthSpinner.id -> {viewModel.currentMonth = position +1
            viewModel.fetchFilteredData()}
            yearSpinner.id -> {viewModel.currentYear = viewModel.years_items.value!![position]
            viewModel.fetchFilteredData()}
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}