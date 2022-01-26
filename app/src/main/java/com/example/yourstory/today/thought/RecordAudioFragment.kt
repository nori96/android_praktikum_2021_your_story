package com.example.yourstory.today.thought

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource.MIC
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.yourstory.R
import com.example.yourstory.databinding.RecordAudioFragmentBinding
import java.io.File
import java.io.IOException
import java.util.*

class RecordAudioFragment : Fragment() {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: RecordAudioFragmentBinding? = null
    private val binding get() = _binding!!
    private var mediaRecorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val fileName = UUID.randomUUID().toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = RecordAudioFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]

        binding.recordAudioRecordButton.setOnClickListener{
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(getRecordingFilePath())
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                try {
                    prepare()
                } catch (e: IOException) { }
                start()
            }
        }
        binding.recordAudioStopButton.setOnClickListener {
            if (mediaRecorder != null) {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null
            }
        }

        binding.recordAudioPlayButton.setOnClickListener {
            player = MediaPlayer().apply {
                try {
                    setDataSource(getRecordingFilePath())
                    prepare()
                    start()
                } catch (e: IOException) { }
            }
        }

        binding.confirmThoughtDialogAudio.setOnClickListener {
            viewModelShared.audio.value = getRecordingFilePath()
            hostFragmentNavController.navigate(R.id.action_recordAudioFragment_to_thought_dialog)
        }
        binding.cancelThoughtDialogAudio.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_recordAudioFragment_to_thought_dialog)
        }
        return binding.root
    }

    private fun getRecordingFilePath():String {
        val musicDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(musicDirectory, "$fileName.3gp")
        return file.path
    }
}