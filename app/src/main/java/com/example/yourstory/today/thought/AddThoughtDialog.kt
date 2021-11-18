package com.example.yourstory.today.thought

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yourstory.R

class AddThoughtDialog : Fragment() {

    companion object {
        fun newInstance() = AddThoughtDialog()
    }

    private lateinit var viewModel: ThoughtDialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.thought_dialog_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ThoughtDialogViewModel::class.java)
        // TODO: Use the ViewModel
    }

}