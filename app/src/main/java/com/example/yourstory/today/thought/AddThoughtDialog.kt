package com.example.yourstory.today.thought

import android.Manifest
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

class AddThoughtDialog : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
        const val PERMISSION_AUDIO_REQUEST_CODE = 2
        const val PERMISSION_CAMERA_REQUEST_CODE = 3
    }

    private lateinit var viewModel: ThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: ThoughtDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ThoughtDialogFragmentBinding.inflate(inflater, container, false)

        hostFragmentNavController = findNavController(this)
        binding.thoughtLocationCardView.setOnClickListener {
            if (hasLocationPermission()) {
                hostFragmentNavController.navigate(R.id.recordLocationFragment)
            } else {
                requestLocationPermission()
            }
        }
        binding.thoughtImageCardView.setOnClickListener {
            if (hasCameraPermission()) {
                hostFragmentNavController.navigate(R.id.takePictureFragment)
            } else {
                requestCameraPermission()
            }
        }
        binding.thoughtTextCardView.setOnClickListener {
            hostFragmentNavController.navigate(R.id.recordTextFragment)
        }
        binding.thoughtVoiceCardView.setOnClickListener {
            if (hasMicrophonePermission()) {
                hostFragmentNavController.navigate(R.id.recordAudioFragment)
            } else {
                requestMicrophonePermission()
            }
        }
        return binding.root
    }

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            resources.getString(R.string.thought_dialog_permission_location),
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
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
            Manifest.permission.ACCESS_FINE_LOCATION)

    private fun hasMicrophonePermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.RECORD_AUDIO)

    private fun hasCameraPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.CAMERA)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ThoughtDialogViewModel::class.java)
        // TODO: Use the ViewModel
    }

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