package com.example.yourstory.today.thought

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.yourstory.R
import com.example.yourstory.databinding.RecordTextFragmentBinding
import org.w3c.dom.Text

class RecordTextFragment : Fragment() {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: RecordTextFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var textView: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RecordTextFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        textView = _binding!!.thoughtRecordedText
        viewModelShared.text.observe(viewLifecycleOwner,{
            newText -> textView.setText(newText, TextView.BufferType.EDITABLE)
        })


        binding.confirmThoughtDialogText.setOnClickListener {
            viewModelShared.text.value = textView.text.toString()
            hostFragmentNavController.navigate(R.id.action_recordTextFragment_to_thought_dialog)
        }
        binding.cancelThoughtDialogText.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_recordTextFragment_to_thought_dialog)
        }

        return binding.root
    }
}