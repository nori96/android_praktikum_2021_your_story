package com.example.yourstory.today.likert

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.yourstory.R
import com.example.yourstory.databinding.LikertDialogFragmentBinding
import com.example.yourstory.databinding.ThoughtDialogFragmentBinding
import com.example.yourstory.today.thought.SharedThoughtDialogViewModel

class LikertDialog : Fragment() {

    private lateinit var likertViewModel: LikertDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: LikertDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LikertDialogFragmentBinding.inflate(inflater, container, false)
        likertViewModel = ViewModelProvider(requireActivity())[LikertDialogViewModel::class.java]

        hostFragmentNavController = NavHostFragment.findNavController(this)
        binding.joySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //binding.startLikertDialog.text = progress.toString()
                Log.i("info",progress.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        return binding.root
    }
}