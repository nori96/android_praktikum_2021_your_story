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
import com.example.yourstory.databinding.TakePictureFragmentBinding

class TakePictureFragment : Fragment() {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: TakePictureFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TakePictureFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        binding.confirmThoughtDialogPicture.setOnClickListener {
            viewModelShared.hasImage.value = true
            hostFragmentNavController.navigate(R.id.action_takePictureFragment_to_thought_dialog)
        }
        binding.cancelThoughtDialogPicture.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_takePictureFragment_to_thought_dialog)
        }

        return binding.root
    }
}