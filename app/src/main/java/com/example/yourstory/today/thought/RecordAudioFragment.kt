package com.example.yourstory.today.thought

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource.MIC
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.yourstory.R
import com.example.yourstory.databinding.RecordAudioFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.util.*

class RecordAudioFragment : Fragment() {

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: RecordAudioFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertDialogBuilder: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = RecordAudioFragmentBinding.inflate(inflater, container, false)
        hostFragmentNavController = NavHostFragment.findNavController(this)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]
        alertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
        binding.recordAudioRecordButton.setOnClickListener{
            stopPlayer()
            viewModelShared.chronometerElapsedTime = 0L
            viewModelShared.audioFileName = getRecordingFilePath()
            viewModelShared.mediaRecorder = MediaRecorder().apply {
                setAudioSource(MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(viewModelShared.audioFileName)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                try {
                    prepare()
                } catch (e: IOException) { }
                start()
                binding.recordAudioTimer.base = SystemClock.elapsedRealtime()
                binding.recordAudioTimer.start()
            }
        }

        binding.recordAudioTimer.base = SystemClock.elapsedRealtime() - viewModelShared.chronometerElapsedTime
        if (viewModelShared.mediaRecorder != null) {
            binding.recordAudioTimer.start()
        }
        binding.recordAudioTimer.setOnChronometerTickListener {
            viewModelShared.chronometerElapsedTime = SystemClock.elapsedRealtime() - binding.recordAudioTimer.base
        }

        binding.recordAudioStopButton.setOnClickListener {
            if (viewModelShared.mediaRecorder != null) {
                viewModelShared.mediaRecorder?.apply {
                    stop()
                    release()
                }
                viewModelShared.mediaRecorder = null
                binding.recordAudioTimer.stop()
            } else {
                alertDialogBuilder.setTitle(R.string.record_audio_no_stop_possible_heading)
                alertDialogBuilder.setMessage(R.string.record_audio_no_stop_possible_main_text)
                alertDialogBuilder.setPositiveButton(R.string.create_report_confirm_dialog) { _, _ -> }
                alertDialogBuilder.show()
            }
        }

        binding.recordAudioPlayButton.setOnClickListener {
            var actionTaken = false
            if (viewModelShared.player  == null ||
               (viewModelShared.player != null && !viewModelShared.player!!.isPlaying)) {
                viewModelShared.player = MediaPlayer().apply {
                    try {
                        setDataSource(viewModelShared.audioFileName)
                        prepare()
                        start()
                        actionTaken = true
                    } catch (e: IOException) { }
                }
            }
            else if (viewModelShared.player != null && viewModelShared.player!!.isPlaying) {
                viewModelShared.player!!.stop()
                actionTaken = true
            }
            if (!actionTaken) {
                alertDialogBuilder.setTitle(R.string.record_audio_no_recording_play_heading)
                alertDialogBuilder.setMessage(R.string.record_audio_no_recording_play_main_text)
                alertDialogBuilder.setPositiveButton(R.string.create_report_confirm_dialog) { _, _ -> }
                alertDialogBuilder.show()
            }
        }

        binding.confirmThoughtDialogAudio.setOnClickListener {
            if (viewModelShared.audioFileName != "") {
                viewModelShared.audio.value = viewModelShared.audioFileName
                viewModelShared.clearAudioData()
                //revertUserActions()
                hostFragmentNavController.navigate(R.id.action_recordAudioFragment_to_thought_dialog)
            } else {
                alertDialogBuilder.setTitle(R.string.record_audio_no_audio_submit_heading)
                alertDialogBuilder.setMessage(R.string.record_audio_no_audio_submit_main_text)
                alertDialogBuilder.setPositiveButton(R.string.create_report_confirm_dialog) { _, _ -> }
                alertDialogBuilder.show()
            }
        }
        binding.cancelThoughtDialogAudio.setOnClickListener {
            //revertUserActions()
            viewModelShared.clearAudioData()
            hostFragmentNavController.navigate(R.id.action_recordAudioFragment_to_thought_dialog)
        }
        return binding.root
    }

    private fun getRecordingFilePath():String {
        val fileName = UUID.randomUUID().toString()
        val musicDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        val file = File(musicDirectory, "$fileName.3gp")
        return file.path
    }

    fun stopPlayer() {
        if (viewModelShared.player != null && viewModelShared.player!!.isPlaying) {
            viewModelShared.player!!.stop()
        }
    }
}