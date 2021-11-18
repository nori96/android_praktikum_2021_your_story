package com.example.yourstory.today.likert

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yourstory.R

class LikertDialog : Fragment() {

    companion object {
        fun newInstance() = LikertDialog()
    }

    private lateinit var viewModel: LikertDialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.likert_dialog_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LikertDialogViewModel::class.java)
        // TODO: Use the ViewModel
    }

}