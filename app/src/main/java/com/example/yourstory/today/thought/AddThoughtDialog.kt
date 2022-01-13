package com.example.yourstory.today.thought

import android.Manifest
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.example.yourstory.R
import com.vmadalin.easypermissions.EasyPermissions
import com.example.yourstory.databinding.ThoughtDialogFragmentBinding
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.yourstory.today.TodayViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddThoughtDialog : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
        const val PERMISSION_AUDIO_REQUEST_CODE = 2
        const val PERMISSION_CAMERA_REQUEST_CODE = 3
    }

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var todayViewModel: TodayViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: ThoughtDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ThoughtDialogFragmentBinding.inflate(inflater, container, false)
        todayViewModel = ViewModelProvider(requireActivity())[TodayViewModel::class.java]
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]
        hostFragmentNavController = findNavController(this)
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        binding.thoughtLocationCardView.setOnClickListener {
            if (hasLocationPermission()) {
                hostFragmentNavController.navigate(R.id.action_thought_dialog_to_recordLocationFragment)
            } else {
                requestLocationPermission()
            }
        }

        binding.thoughtImageCardView.setOnClickListener {
            if (hasCameraPermission()) {
                hostFragmentNavController.navigate(R.id.action_thought_dialog_to_takePictureFragment)
            } else {
                requestCameraPermission()
            }
        }

        binding.thoughtTextCardView.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_thought_dialog_to_recordTextFragment)
        }

        binding.thoughtVoiceCardView.setOnClickListener {
            if (hasMicrophonePermission()) {
                hostFragmentNavController.navigate(R.id.action_thought_dialog_to_recordAudioFragment)
            } else {
                requestMicrophonePermission()
            }
        }

        binding.confirmThoughtDialog.setOnClickListener {
            if(!viewModelShared.checkIfAnySelected()){
                materialAlertDialogBuilder.setTitle(R.string.thought_empty_dialog_title)
                materialAlertDialogBuilder.setMessage(R.string.thought_empty_dialog_text)
                materialAlertDialogBuilder.setPositiveButton("OK"){
                        dialog, which ->
                }
                materialAlertDialogBuilder.show()
            }else {
                viewModelShared.confirmDiaryEntry(requireContext())
                hostFragmentNavController.navigate(R.id.action_thought_dialog_to_navigation_today)
            }
        }
        binding.cancelThoughtDialog.setOnClickListener {
            hostFragmentNavController.navigate(R.id.action_thought_dialog_to_navigation_today)
        }

        viewModelShared.location.observe(viewLifecycleOwner, { location ->
            if (location != LatLng(0.0,0.0)) {
                binding.cancelThoughtLocationCardView.visibility = View.VISIBLE
                binding.cancelThoughtLocationCardViewIcon.visibility = View.VISIBLE
            } else {
                binding.cancelThoughtLocationCardView.visibility = View.INVISIBLE
                binding.cancelThoughtLocationCardViewIcon.visibility = View.INVISIBLE
            }
        })
        binding.cancelThoughtLocationCardViewIcon.setOnClickListener {
            viewModelShared.location.value = LatLng(0.0,0.0)
        }

        viewModelShared.image.observe(viewLifecycleOwner, { image ->
            if (image.height != 1) {
                binding.cancelThoughtImageCardView.visibility = View.VISIBLE
                binding.cancelThoughtImageCardViewIcon.visibility = View.VISIBLE
            } else {
                binding.cancelThoughtImageCardView.visibility = View.INVISIBLE
                binding.cancelThoughtImageCardViewIcon.visibility = View.INVISIBLE
            }
        })
        binding.cancelThoughtImageCardViewIcon.setOnClickListener {
            viewModelShared.image.value = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888)
        }

        viewModelShared.audio.observe(viewLifecycleOwner, { audio ->
            if (!audio.isBlank()) {
                binding.cancelThoughtAudioCardView.visibility = View.VISIBLE
                binding.cancelThoughtAudioCardViewIcon.visibility = View.VISIBLE
            } else {
                binding.cancelThoughtAudioCardView.visibility = View.INVISIBLE
                binding.cancelThoughtAudioCardViewIcon.visibility = View.INVISIBLE
            }
        })
        binding.cancelThoughtAudioCardViewIcon.setOnClickListener {
            viewModelShared.audio.value = ""
        }

        viewModelShared.text.observe(viewLifecycleOwner, { text ->
            if (!text.isBlank()) {
                binding.cancelThoughtTextCardView.visibility = View.VISIBLE
                binding.cancelThoughtTextCardViewIcon.visibility = View.VISIBLE
            } else {
                binding.cancelThoughtTextCardView.visibility = View.INVISIBLE
                binding.cancelThoughtTextCardViewIcon.visibility = View.INVISIBLE
            }
        })
        binding.cancelThoughtTextCardViewIcon.setOnClickListener {
            viewModelShared.text.value = ""
        }

        return binding.root
    }

    override fun onDestroyView(){
        super.onDestroyView()
    }

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            resources.getString(R.string.thought_dialog_permission_location),
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private fun requestCameraPermission() {
        EasyPermissions.requestPermissions(
            this,
            resources.getString(R.string.thought_dialog_permission_camera),
            PERMISSION_CAMERA_REQUEST_CODE,
            Manifest.permission.CAMERA
        )
    }

    private fun requestMicrophonePermission() {
        EasyPermissions.requestPermissions(
            this,
            resources.getString(R.string.thought_dialog_permission_audio),
            PERMISSION_AUDIO_REQUEST_CODE,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) &&
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun hasMicrophonePermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.RECORD_AUDIO)

    private fun hasCameraPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.CAMERA)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (requestCode == 1) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                SettingsDialog.Builder(requireActivity())
                    .rationale(R.string.thought_dialog_permission_location_permanently)
                    .build()
                    .show()
            } else {
                requestLocationPermission()
            }
        }
        if (requestCode == 2) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                SettingsDialog.Builder(requireActivity())
                    .rationale(R.string.thought_dialog_permission_audio_permanently)
                    .build()
                    .show()
            } else {
                requestMicrophonePermission()
            }
        }
        if (requestCode == 3) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                SettingsDialog.Builder(requireActivity())
                    .rationale(R.string.thought_dialog_permission_camera_permanently)
                    .build()
                    .show()
            } else {
                requestCameraPermission()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == 1) {
            hostFragmentNavController.navigate(R.id.recordLocationFragment)
        }
        if (requestCode == 2) {
            hostFragmentNavController.navigate(R.id.recordAudioFragment)
        }
        if (requestCode == 3) {
            hostFragmentNavController.navigate(R.id.takePictureFragment)
        }
    }
}