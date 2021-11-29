package com.example.yourstory.today

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yourstory.R
import com.example.yourstory.databinding.TodayFragmentBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TodayFragment : Fragment() {

    companion object {
        fun newInstance() = TodayFragment()
    }

    private lateinit var hostFramentNavController: NavController
    private lateinit var viewModel: TodayViewModel
    private lateinit var likertFab: FloatingActionButton
    private lateinit var thoughtFab: FloatingActionButton
    private var fabClicked = false

    //private var layoutManager: RecyclerView.LayoutManager? = null
    //private var todayAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    // TodayFragmentBinding is a generated datatype...
    private lateinit var binding: TodayFragmentBinding
    // This property is only valid between onCreateView and
    // onDestroyView.


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = TodayFragmentBinding.inflate(inflater, container, false)

        if (container != null) {
            hostFramentNavController = container.findNavController()
        }

        likertFab = binding.likertFab!!
        thoughtFab = binding.thoughtFab!!

        binding.rootFab?.setOnClickListener {
            if (!fabClicked) {
                likertFab.visibility = View.VISIBLE
                thoughtFab.visibility = View.VISIBLE
            } else {
                likertFab.visibility = View.INVISIBLE
                thoughtFab.visibility = View.INVISIBLE
            }
            fabClicked = !fabClicked
        }
        binding.thoughtFab?.setOnClickListener {
            hostFramentNavController.navigate(R.id.thought_dialog)
        }
        binding.likertFab?.setOnClickListener {
            hostFramentNavController.navigate(R.id.likertDialog)
        }

        return binding.root
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
    }

}