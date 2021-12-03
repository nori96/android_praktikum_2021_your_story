package com.example.yourstory.today.thought

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.yourstory.R
import com.example.yourstory.databinding.RecordTextFragmentBinding

class RecordTextFragment : Fragment() {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: RecordTextFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RecordTextFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        binding.confirmThoughtDialogText.setOnClickListener {
            viewModelShared.text.value = "I only wanted to send out a warning, against the needless waste created by capitalism without philosophy, the needless colonisation of planets, the needless circulation of slanted media, and needlessly tall buildings that symbolise all of this!"
            hostFragmentNavController.navigate(R.id.action_recordTextFragment_to_thought_dialog)
        }
        binding.cancelThoughtDialogText.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_recordTextFragment_to_thought_dialog)
        }

        return binding.root
    }
}