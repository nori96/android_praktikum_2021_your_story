package com.example.yourstory.diary.detail

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.yourstory.MainActivity
import com.example.yourstory.R
import com.example.yourstory.databinding.ActivityMainBinding
import com.example.yourstory.databinding.DiaryDetailFragmentBinding

class DiaryDetailFragment : Fragment() {

    private val args: DiaryDetailFragmentArgs by navArgs()
    private lateinit var viewModel: DiaryDetailViewModel
    private lateinit var binding: DiaryDetailFragmentBinding
    lateinit var testText: TextView

    companion object {
        fun newInstance() = DiaryDetailFragment()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DiaryDetailFragmentBinding.inflate(layoutInflater,container,false)

        //Set date as the title
        (requireActivity() as MainActivity).toolbar!!.title = args.date.toString()

        testText = binding.testText
        testText.text = args.date.toString()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DiaryDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}