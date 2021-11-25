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
import com.example.yourstory.databinding.RecordAudioFragmentBinding
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
        viewModelShared = ViewModelProvider(requireActivity()).get(SharedThoughtDialogViewModel::class.java)

        binding.confirmThoughtDialogText.setOnClickListener {
            viewModelShared.hasText.value = true
            hostFragmentNavController.navigate(R.id.thought_dialog)
        }
        return binding.root
    }
}