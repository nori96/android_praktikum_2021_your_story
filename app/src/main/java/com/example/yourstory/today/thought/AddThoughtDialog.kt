package com.example.yourstory.today.thought

import android.Manifest
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.yourstory.R
import com.vmadalin.easypermissions.EasyPermissions
import com.example.yourstory.databinding.ThoughtDialogFragmentBinding
import com.vmadalin.easypermissions.dialogs.SettingsDialog

class AddThoughtDialog : Fragment(), EasyPermissions.PermissionCallbacks {


    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
        const val PERMISSION_AUDIO_REQUEST_CODE = 2
        const val PERMISSION_CAMERA_REQUEST_CODE = 3
    }

    private lateinit var viewModel: ThoughtDialogViewModel
    private var _binding: ThoughtDialogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ThoughtDialogFragmentBinding.inflate(inflater, container, false)

        /*if(EasyPermissions.permissionPermanentlyDenied(this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            binding.thoughtLocationCardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.non_clickable_grey))
        }*/

        binding.thoughtLocationCardView.setOnClickListener {
            requestLocationPermission()
        }
        binding.thoughtImageCardView.setOnClickListener {
            requestCameraPermission()
        }
        binding.thoughtTextCardView.setOnClickListener {

        }
        binding.thoughtVoiceCardView.setOnClickListener {

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

    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
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
        if (requestCode == 1)
        {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                SettingsDialog.Builder(requireActivity())
                    .rationale(R.string.thought_dialog_permission_location)
                    .build()
                    .show()
            }
        }
        if (requestCode == 3)
        {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                SettingsDialog.Builder(requireActivity())
                    .rationale(R.string.thought_dialog_heading)
                    .build()
                    .show()
            }
        }
        //requestLocationPermission()
        //EasyPermissions.somePermissionPermanentlyDenied(this, perms)
        //handleLocationDialog()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        TODO("Not yet implemented")
    }
    /*private fun handleLocationDialog() {
        if (hasLocationPermission()) {
            //binding.locationCardView.setCardBackgroundColor(resources.getColor(R.color.non_clickable_grey))

        } else {
            binding.thoughtLocationCardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.non_clickable_grey))
        }
    }*/
}