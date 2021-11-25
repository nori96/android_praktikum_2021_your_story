package com.example.yourstory.today.thought

import android.Manifest
import android.opengl.Visibility
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
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

    private lateinit var viewModelShared: SharedThoughtDialogViewModel
    private lateinit var hostFragmentNavController: NavController
    private var _binding: ThoughtDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ThoughtDialogFragmentBinding.inflate(inflater, container, false)
        viewModelShared = ViewModelProvider(requireActivity())[SharedThoughtDialogViewModel::class.java]
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
        binding.confirmThoughtDialog.setOnClickListener {
            // TODO some action on the database
            hostFragmentNavController.navigate(R.id.navigation_today)
        }
        binding.cancelThoughtDialog.setOnClickListener {
            hostFragmentNavController.navigate(R.id.navigation_today)
        }


        viewModelShared.hasLocation.observe(viewLifecycleOwner, { location ->
            if (location) {
                binding.cancelThoughtLocationCardView.visibility = View.VISIBLE
                binding.cancelThoughtLocationCardViewIcon.visibility = View.VISIBLE
            } else {
                binding.cancelThoughtLocationCardView.visibility = View.INVISIBLE
                binding.cancelThoughtLocationCardViewIcon.visibility = View.INVISIBLE
            }
        })
        binding.cancelThoughtLocationCardViewIcon.setOnClickListener {
            viewModelShared.hasLocation.value = false
        }

        viewModelShared.hasImage.observe(viewLifecycleOwner, { image ->
            if (image) {
                binding.cancelThoughtImageCardView.visibility = View.VISIBLE
                binding.cancelThoughtImageCardViewIcon.visibility = View.VISIBLE
            } else {
                binding.cancelThoughtImageCardView.visibility = View.INVISIBLE
                binding.cancelThoughtImageCardViewIcon.visibility = View.INVISIBLE
            }
        })
        binding.cancelThoughtImageCardViewIcon.setOnClickListener {
            viewModelShared.hasImage.value = false
        }

        viewModelShared.hasAudio.observe(viewLifecycleOwner, { audio ->
            if (audio) {
                Log.i("asdff", viewModelShared.hasAudio.value.toString());
                binding.cancelThoughtAudioCardView.visibility = View.VISIBLE
                binding.cancelThoughtAudioCardViewIcon.visibility = View.VISIBLE
            } else {
                Log.i("asdfq", viewModelShared.hasAudio.value.toString());
                binding.cancelThoughtAudioCardView.visibility = View.INVISIBLE
                binding.cancelThoughtAudioCardViewIcon.visibility = View.INVISIBLE
            }
        })
        binding.cancelThoughtAudioCardViewIcon.setOnClickListener {
            viewModelShared.hasAudio.value = false
            Log.i("asdf", viewModelShared.hasAudio.value.toString());
        }

        viewModelShared.hasText.observe(viewLifecycleOwner, { text ->
            if (text) {
                binding.cancelThoughtTextCardView.visibility = View.VISIBLE
                binding.cancelThoughtTextCardViewIcon.visibility = View.VISIBLE
            } else {
                binding.cancelThoughtTextCardView.visibility = View.INVISIBLE
                binding.cancelThoughtTextCardViewIcon.visibility = View.INVISIBLE
            }
        })
        binding.cancelThoughtTextCardViewIcon.setOnClickListener {
            viewModelShared.hasText.value = false
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
        viewModelShared = ViewModelProvider(this).get(SharedThoughtDialogViewModel::class.java)
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