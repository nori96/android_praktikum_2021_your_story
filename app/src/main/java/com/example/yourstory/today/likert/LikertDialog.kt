package com.example.yourstory.today.likert

import android.R.attr
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.view.marginLeft
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.yourstory.R
import com.example.yourstory.database.data.EmotionalState
import com.example.yourstory.databinding.LikertDialogFragmentBinding
import com.example.yourstory.utils.DateEpochConverter
import androidx.constraintlayout.widget.ConstraintLayout


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
        binding.cancelLikertDialog.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_likertDialog_to_navigation_today)
        }

        binding.confirmLikertDialog.setOnClickListener {
            submitLikert()
            hostFragmentNavController.navigate(R.id.action_likertDialog_to_navigation_today)
        }
        /*val seekbarWidth = binding.sadnessSeekBar.width
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins((seekbarWidth * 0.2).toInt(), 0, 0, 0)
        binding.likertDialogIntensity1.layoutParams = params*/
        //binding.likertDialogIntensity1.marginLeft = (seekbarWidth * 0.2).toInt()

        return binding.root
    }

    private fun submitLikert() {
        likertViewModel.addEmotionalState(EmotionalState(0,
            DateEpochConverter.generateEpochDate(),
            binding.joySeekBar.progress,
            binding.surpriseSeekBar.progress,
            binding.angerSeekBar.progress,
            binding.sadnessSeekBar.progress,
            binding.fearSeekBar.progress,
            binding.disgustSeekBar.progress))
    }
}