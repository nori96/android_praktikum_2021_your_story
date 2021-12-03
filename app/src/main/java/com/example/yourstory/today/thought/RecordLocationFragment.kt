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
import com.example.yourstory.databinding.RecordLocationFragmentBinding

class RecordLocationFragment : Fragment() {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: RecordLocationFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RecordLocationFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        binding.confirmThoughtDialogLocation.setOnClickListener {
            viewModelShared.location.value = "10"
            hostFragmentNavController.navigate(R.id.action_recordLocationFragment_to_thought_dialog)
        }

        binding.cancelThoughtDialogLocation.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_recordLocationFragment_to_thought_dialog)
        }

        return binding.root
    }
}