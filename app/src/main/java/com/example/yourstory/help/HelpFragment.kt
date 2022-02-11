package com.example.yourstory.help

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yourstory.R
import com.example.yourstory.databinding.HelpFragmentBinding
import com.example.yourstory.databinding.ReportsFragmentBinding

class HelpFragment : Fragment() {

    private lateinit var binding: HelpFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HelpFragmentBinding.inflate(inflater, container, false)
        binding.helpPageContentText.movementMethod = ScrollingMovementMethod()
        return binding.root
    }
}