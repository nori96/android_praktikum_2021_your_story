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
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.databinding.LikertDialogFragmentBinding
import java.text.SimpleDateFormat
import java.util.*

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

        binding.confirmLikertDialog.setOnClickListener {
            submitLikert()
        }
        return binding.root
    }

    private fun submitLikert() {
        var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        likertViewModel.addEmotionalState(EmotionalState(0,
            simpleDateFormat.format(Date()).toString(),
            binding.joySeekBar.progress,
            binding.surpriseSeekBar.progress,
            binding.angerSeekBar.progress,
            binding.sadnessSeekBar.progress,
            binding.fearSeekBar.progress,
            binding.disgustSeekBar.progress))
    }
}