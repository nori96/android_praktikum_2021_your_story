package com.example.yourstory.today

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.databinding.TodayFragmentBinding

class TodayFragment : Fragment() {

    companion object {
        fun newInstance() = TodayFragment()
    }

    private lateinit var viewModel: TodayViewModel

    //private var layoutManager: RecyclerView.LayoutManager? = null
    //private var todayAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    // TodayFragmentBinding is a generated datatype...
    private var _binding: TodayFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TodayFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        /*binding.recyclerViewTodayPage.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            todayAdapter = RecyclerAdapter()
        }*/
        //binding.recyclerViewTodayPage.layoutManager = LinearLayoutManager(activity)
        binding.recyclerViewTodayPage.adapter = RecyclerAdapter()
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProvider(this).get(TodayViewModel::class.java)
        // TODO: Use the ViewModel
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}